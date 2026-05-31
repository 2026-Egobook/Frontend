# ADR-0001: RadarDialog 중복 로딩 제거

## 상태
완료

## 컨텍스트
radar 아이콘 클릭 시 두 가지 로딩 UI가 동시에 표시되었다.

1. **HomeFragment `flLoading`** (어두운 반투명 오버레이): `isLoading = true`일 때 표시
2. **RadarDialog 내부** (노랑 배경 + 핑크 ProgressBar): `isLoading` 또는 `isTendenciesLoading`을 관찰

`RadarDialog.onViewCreated`에서 `viewModel.fetchTendencies()`를 호출했고, 이 함수가 공유 `showLoading()/hideLoading()`을 사용하여 두 곳 모두에서 로딩 UI가 활성화되었다.

## 결정
- `RadarDialog`에서 `viewModel.fetchTendencies()` 호출을 제거한다.
- `HomeViewModel.fetchInitialData()`가 앱 시작 시 이미 tendencies를 로드하므로, Dialog 오픈 시점엔 데이터가 준비되어 있다.
- RadarDialog는 캐시된 `viewModel.tendencies` StateFlow를 관찰하여 데이터를 표시한다.
- 예외적으로 데이터가 비어있는 상태(초기 로드 실패 또는 경쟁 상태)에서는 `isLoading`을 폴백으로 관찰하여 progressBar를 표시한다.

## 결과
- radar 클릭 시 `flLoading`(어두운 오버레이)만 표시되지 않고, 즉시 데이터가 보인다.
- RadarDialog의 노랑+핑크 progressBar는 초기 데이터 미준비 시에만 폴백으로 표시된다.
- `isTendenciesLoading` 별도 상태는 불필요하여 제거했다.

## 트레이드오프
- RadarDialog가 매번 최신 데이터를 fetch하지 않으므로, 앱 실행 중 tendency 데이터가 변경된 경우 즉각 반영되지 않는다.
- 향후 UiState sealed class(Loading/Success/Error) 패턴 도입 시 개선 가능하다.
