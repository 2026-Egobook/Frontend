# ADR 0004: PsychologyDialog 표시 방식 개선

## 상태
완료

## 컨텍스트
물병 아이콘 클릭 시 열리는 `PsychologyDialog`에 세 가지 문제가 있었다.

1. **슬라이드 다운 애니메이션**: `setWindowAnimations(0)`은 Material3 테마의 `android:windowAnimationStyle`을 override하지 못한다. `0`은 "unset"으로 처리되어 테마 애니메이션이 그대로 적용됐다.
2. **이중 어두움**: `applyScreenBlur`로 블러 오버레이가 적용된 상태에서 Android 기본 dim(~0.6)이 추가로 적용돼 배경이 과도하게 어두워졌다.
3. **다이어로그 위치 이동**: 로딩 중 텍스트뷰가 비어 다이어로그가 작고 상단에 위치했다가, 데이터 로드 후 콘텐츠가 채워지며 아래로 커지는 현상이 발생했다.

## 결정

### 애니메이션 억제
`setWindowAnimations(0)`은 효과가 없으므로, `onCreate`에서 `setStyle(STYLE_NORMAL, Theme.App.Dialog.NoAnimation)`으로 Window 생성 전에 테마 자체를 교체한다. `DialogNoAnimation` 스타일은 `windowEnterAnimation`/`windowExitAnimation`을 `@null`로 명시해 Material3 기본 애니메이션을 완전히 차단한다.

### dim 제거
블러 오버레이가 배경 어두움을 전담하므로 시스템 dim은 `backgroundDimAmount=0`으로 완전히 제거한다. 두 레이어를 중복 적용할 이유가 없다.

### 위치 안정화
Window와 루트 FrameLayout을 `MATCH_PARENT`로 설정하고 카드 LinearLayout에 `layout_gravity="center"`를 적용한다. Window가 항상 전체 화면을 차지하므로 콘텐츠 크기 변동에 무관하게 위치가 고정된다. `setLayout(MATCH_PARENT, MATCH_PARENT)`는 `onStart`에서 `super.onStart()` 이후 호출해야 DialogFragment의 기본 `WRAP_CONTENT` 파라미터를 덮어쓸 수 있다.

### blur 해제 책임
`removeScreenBlur()`를 버튼 클릭 핸들러가 아닌 `onDestroyView`에서 호출해 dismiss 경로에 무관하게 blur가 반드시 해제되도록 한다.

## 결과
- 다이어로그가 애니메이션 없이 즉시 화면 중앙에 고정되어 나타난다.
- 블러만으로 배경이 처리되며 이중 어두움이 없다.
- 로딩 완료 후 콘텐츠가 채워져도 위치가 변하지 않는다.
- blur 해제가 `onDestroyView`에서 보장된다.

## 트레이드오프
- Window가 `MATCH_PARENT`이므로 다이어로그 외부 영역 터치가 Window에 흡수된다. `isCancelable=false` 정책과 맞물려 의도된 동작이나, 추후 cancelable 다이어로그에 이 패턴을 적용할 경우 터치 처리를 별도로 구현해야 한다.
- `backgroundDimAmount=0`은 시스템의 `FLAG_DIM_BEHIND` 동작을 실질적으로 비활성화한다. 블러 오버레이가 없는 다른 다이어로그에 이 테마를 재사용하면 배경 처리가 없어 보일 수 있다.
