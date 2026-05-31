# ADR 0003: 닉네임 수정 기능 구현

## 상태
Accepted

## 컨텍스트
계정 화면에서 사용자가 닉네임을 수정할 수 있어야 한다. 서버는 `PATCH /users/nickname` 엔드포인트를 제공하며, 닉네임은 2~8자 한글/영문/숫자만 허용된다. `GET /home` 응답에 `nickname` 필드가 포함되어 있어 이를 닉네임 조회 소스로 활용한다.

## 결정

### 닉네임 조회 소스
초기에는 `UserInfoStorage`(DataStore)에 로컬 저장하는 방식을 시도했으나, 앱 최초 진입 시 DataStore가 비어 있어 닉네임이 표시되지 않는 문제가 있었다. `GET /home` API 응답(`UserDto.nickname`)에 닉네임이 포함되어 있으므로 `AccountViewModel`에서 `UserRepository.load()`를 호출해 서버에서 직접 읽도록 변경한다. PATCH 성공 후에는 낙관적으로 `_nickname` StateFlow를 갱신하며 추가 API 호출 없이 UI를 업데이트한다.

### 닉네임 검증 위치
`NicknameValidator`를 `domain/model/account`에 배치하고, ViewModel에서 API 호출 전 검증을 수행한다. UseCase 레이어가 없는 현재 프로젝트 구조에서 ViewModel이 검증을 담당하는 것이 가장 현실적인 선택이다.

### 블러 해제 방식
`btnEditNickname` 클릭 시 `applyScreenBlur()`(Activity-level 블러)를 사용하므로, 다이얼로그 dismiss 시 반드시 `removeScreenBlur()`로 해제해야 한다. 초기에 `setFragmentResult` → `AccountFragment.clearBlur()`(fragment-local BlurView만 숨김) 방식을 시도했으나, Activity 블러가 해제되지 않는 버그가 있었다. `NicknameEditDialogFragment.onDismiss()`에서 직접 `removeScreenBlur()`를 호출하는 방식으로 수정한다(`AccountDeleteDialog1Fragment`와 동일한 패턴).

### PATCH 응답 파싱
Retrofit이 Gson 컨버터를 사용하기 때문에 `ApiResponse<String>`으로 선언 시 서버가 `data` 필드를 객체 또는 null로 반환하면 `IllegalStateException: Expected a string but was BEGIN_OBJECT`가 발생한다. `data` 값을 사용하지 않으므로 Gson이 모든 JSON 타입을 수용할 수 있는 `ApiResponse<JsonElement?>`로 선언한다.

### Toast 이벤트 구독
`nicknameToastEvent`를 `AccountFragment`와 `NicknameEditDialogFragment` 양쪽에서 구독하면 toast가 두 번 표시된다. `AccountFragment` 단일 구독으로 통일하고 다이얼로그에서는 구독을 제거한다.

### SharedFlow replay 통일
기존 `_linkToastEvent(replay=1)`을 `replay=0`으로 변경한다. `replay=1`은 화면 재구독 시 이전 Toast가 재발행되는 UX 버그를 유발하므로 모든 Toast 이벤트를 `replay=0`으로 통일한다.

## 결과
- `NicknameValidator` + `NicknameValidatorTest`로 핵심 검증 로직이 TDD로 보호된다.
- 닉네임을 `GET /home` API에서 로드하므로 항상 서버의 최신 값이 표시된다.
- 다이얼로그 dismiss(뒤로가기, 외부 터치, 수정 완료) 모두에서 Activity 블러가 올바르게 해제된다.
- API 로딩 중 수정하기 버튼이 비활성화되어 중복 요청이 방지된다.
- toast가 단 한 번만 표시된다.
