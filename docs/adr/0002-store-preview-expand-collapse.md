# ADR-0002: 상점 프리뷰 전체보기 확장/축소 기능

## 상태
완료

## 컨텍스트
상점(StoreFragment)의 거북이 프리뷰 영역이 화면 상단 일부만 차지하고 있어, 아이템을 착용했을 때 전체 모습을 확인하기 어려웠다. 하단 탭/구매 패널을 숨기고 프리뷰를 전체 화면으로 확장하는 기능이 필요했다.

## 결정

### 1. 상태 모델: `PreviewExpansionState` enum
Fragment 인스턴스 변수(`Boolean`)를 사용하는 대신 `PreviewExpansionState` enum을 별도 파일로 추출했다.

- `toggle()`, `isExpanded` 를 enum 내부에 캡슐화해 순수 함수로 테스트 가능
- `onSaveInstanceState` + `savedInstanceState`로 화면 회전 시 상태 복원

### 2. 레이아웃 전환: ConstraintSet + TransitionSet
확장/축소 시 두 가지 변화가 동시에 일어난다.

- **visibility 변경**: 하단 패널 6개 뷰 (`iv_tab_layout_background`, `tl_tabs`, `vp2_store_collection_container`, `ll_collection_controller`, `iv_reset`, `tv_purchase`)
- **constraint 변경**: 배경 이미지, 거북이 이미지, 버튼의 bottom anchor를 parent 또는 하단 패널로 전환

`ConstraintSet.clone(binding.root)` → 수정 → `applyTo(binding.root)` 패턴을 사용했다.

### 3. 트랜지션: ChangeBounds + Fade 조합
`ChangeBounds`만 사용하면 visibility 전환이 즉시(팝) 일어나 어색하다. `TransitionSet`으로 `ChangeBounds`와 `Fade`를 병행하여 크기 변화와 페이드 인/아웃이 동시에 부드럽게 동작하도록 했다.

### 4. 즉시 복원: `applyPreviewLayout`
애니메이션 없이 레이아웃만 적용하는 `applyPreviewLayout`을 추출해, 상태 복원 시(화면 회전)와 애니메이션 전환 시 로직을 공유했다.

## 기각된 대안

- **ViewModel StateFlow로 상태 관리**: 이 상태는 UI 전용이며 서버 데이터와 무관하다. `onSaveInstanceState`로 충분히 복원 가능하므로 ViewModel 오염을 피했다.
- **별도 Fragment/Dialog로 전체보기 구현**: 화면 전환 비용과 거북이 이미지 재로딩 문제로 기각. 같은 Fragment 내에서 constraint 변경으로 해결했다.

## 결과
- 버튼 클릭으로 프리뷰가 전체 화면으로 확장/축소됨
- 화면 회전 시 expanded 상태가 유지됨
- `PreviewExpansionState` 단위 테스트 4개 추가
