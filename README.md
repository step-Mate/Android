<a href="https://play.google.com/store/apps/details?id=com.stepmate.app">
	<img src="https://img.shields.io/badge/PlayStore-v1.0.3-4285F4?style=for-the-badge&logo=googleplay&logoColor=white&link=https://play.google.com/store/apps/details?id=com.stepmate.app" />
</a>

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-8.3.0-green.svg)](https://gradle.org/)
[![minSdkVersion](https://img.shields.io/badge/minSdkVersion-29-red)](https://developer.android.com/distribute/best-practices/develop/target-sdk)
[![targetSdkVersion](https://img.shields.io/badge/targetSdkVersion-34-orange)](https://developer.android.com/distribute/best-practices/develop/target-sdk)

# Introduction

사용자의 디바이스의 보행계수기 센서에서 실시간 걸음수를 수집하여,

일, 월, 년간 데이터를 차트로 목표치 대비 걸음수를 칼로리(kcal), 거리(km), 시간(분) 으로 변환하여 함께 보여줍니다.

다른 사용자들과 한달 동안의 걸음수로 경쟁할 수 있으며,

일정 걸음수를 달성할 때 마다 해당되는 미션들을 클리어 함으로써

단순한 걷기를 넘어서 좀 더 재미를 담아 건강 관리에 도움을 주는 앱 입니다.

# Operations

- **걸음수 수집** : 사용자는 실시간으로 수집된 걸음수를 바탕으로 칼로리(kcal), 거리(km), 시간(분) 단위로 변환하여 시각화 된 차트(chart) 로 세분화 하여 볼 수 있다.
- **랭킹 기능** : 다른 사용자들과 한달 동안의 걸음수에 대해 경쟁할 수 있고, 친구 추가를 하여 친구관계로 따로 분류해서 볼 수 있다.
- **미션 기능** : 사용자는 실시간으로 수집된 걸음수를 바탕으로 만들어진 미션들의 조건에 충족하면 “미션 클리어 알림” 을 수신하고, 해당 미션의 보상으로 경험치와 칭호를 획득할 수 있으며 완성된 미션과 미완성 미션을 확인할 수 있다.
- **내 정보 변경** : 사용자의 신체 정보, 닉네임 과 미션 달성시 획득한 칭호들을 변경할 수 있고, 로그아웃과 회원탈퇴를 수행할 수 있다.

# Stacks

| Category | Skill Set |
| ----- | ----- |
| Language | Kotlin |
| UI toolkit | Compose |
| Architecture | Clean Architecture |
| Design Pattern | MVVM, Factory Pattern, Composite Pattern |
| Android | Activity, Service, Lifecycle, Compose-Navigation, HealthConnect, AlarmManager, WorkManager |
| Asynchronous | Kotlinx.Coroutines, Kotlinx.Coroutines.Flow |
| Dependency Injection | Hilt |
| Data | Room, DataStore(proto3), Retrofit2 & Okhttp3 |
| Unit Test | Junit, Kotest, mockk |

# As-Is / Challenge / To-Be

<details>
<summary>포그라운드 서비스의 특성을 활용하여 실시간으로 걸음수를 수집 및 필터링하여 헬스커넥트와 서버로 저장하고 가져오는 걸음수 수집 플로우 구현 후 단위테스트 작성</summary>
<div markdown="1">

### As-Is
- 디바이스의 하드웨어 센서로 부터 걸음수를 수집해야 한다.
- 수집된 걸음수를 기반으로 년/월/일 간 걸음수를 차트(막대 그래프)로 볼 수 있어야 한다.
- 백그라운드에서 실시간으로 수집되어야 하고, 도즈모드에도 동작해야 한다.
- 걸음수 수집 플로우에 대해 검증을 위한 단위 테스트 작성이 필요하다.

### Challenge
- **걸음수 데이터**
  - **데이터 출처**
    - 안드로이드에서 걸음수를 수집하는 방법은 걸음수 감지 센서와 걸음수 측정기 센서를 이용할 수 있습니다.
    - 걸음수 감지 센서는 걸음이 발생할 때 마다 1의 값을 콜백 받는데, 이보다 측정기 센서의 값이 더 정확하여 측정기 센서값을 이용하였습니다.
  - **데이터 관리**
    - 걸음수 데이터는 필요한 형태(년/월/일 등)로 가공되어야 하기 때문에 이러한 다양한 API 를 제공하는 Health Connect 에 저장하여 관리하였습니다.
- **걸음수 수집**
  - 걸음수는 **백그라운드에서 실시간으로 수집** 되어야 하고, **도즈모드**에도 동작해야만 합니다.
  - 또한, 오늘 얼마만큼을 걸었는지를 실시간으로 보여주기 위해 **Foreground Service** 에서 수집하고 **Notification#setOngoing(true)** 로 보여주도록 구현 하였습니다.
  - 걸음수는 사용성 측면에서 보았을 때, 횡단보도에서 신호를 기다린 후 다음 횡단보도 까지 보행을 쉬지 않고 유지합니다.
  - 따라서, 코루틴을 활용한 타이머를 이용하여 1분 동안 걸음이 발생하지 않았다면, 현재 까지 쌓인 "**분할 걸음수**" 를 헬스커넥트에 저장합니다.
  - 걸음이 계속 발생한다면, 반복적으로 타이머의 시간을 1분으로 설정합니다.
- **걸음수 수집 플로우**
  - 기본적으로 다음 공식으로 오늘의 걸음수를 계산할 수 있습니다.
    - **오늘의 걸음수 = 걸음수 측정기 센서값 - 어제 걸음수 + 재부팅 전 걸음수**
  - 걸음수 수집 플로우에서 중요한 시나리오는 다음 3가지 입니다.
    - "분할 걸음수" 가 저장되지 않은 상태에서 **Foreground Service** 가 프로세스에 의해 종료 후 재시작 되었을 경우
      - DataStore 로 걸음이 발생할 때 마다 **오늘 걸음수**를 저장합니다.
      - **Foreground Service** 가 프로세스에 의해 종료 후 재시작 되었다면 **onStartCommand(Intent) 의 intent 가 null** 입니다
      - 이 때 **헬스커넥트**에 저장된 오늘 걸음수와 **DataStore** 에 저장된 오늘 걸음수를 비교하여 **차이만큼 헬스커넥트에 저장**해 줍니다.
    - 하루가 지났을 때
      - 자정이 되면 정시에 동작해야 하므로 **AlarmManager#setAlarmClock** 을 이용하였고, 쌓여진 "분할 걸음수" 를 헬스커넥트에 저장하고, **어제 걸음수** 에 **걸음수 측정기 센서값** 을 저장합니다.
      - 바뀌어진 값들로 오늘 걸음수를 계산하면 0 이 됩니다.
    - 디바이스가 재부팅 되었을 때
      - 디바이스가 재부팅 되면, **걸음수 측정기 센서**의 값은 0 이 됩니다.
      - 만약 앱이 설치된 상태에서 디바이스가 재부팅 되었다면, 재부팅 전 까지의 **오늘 걸음수** 를 **재부팅 전 걸음수** 로 저장하고 이를 **오늘 걸음수** 계산에 이용합니다.
      - 재부팅을 하지 않았다면, 기본적으로 **재부팅 전 걸음수** 는 0 이 됩니다.
- 알고리즘 검증을 위해 [단위테스트](https://github.com/step-Mate/Android/blob/main/feature/home/src/test/java/com/stepmate/home/StepSensorViewModelTest.kt)를 Kotlin 기반 라이브러리인 **Kotest** 와 **mockk** 를 이용하여 작성하였습니다.

### To-Be
- 안드로이드에서 백그라운드 작업에 대한 제한을 강화하면서 Notification 이라는 UI 가 제공되는 Foreground Service 의 이용을 권고하는 업데이트 방향에 대해 알게 되었습니다.
- 앱의 핵심 기능에는 UX를 위해 정확한 검증을 하는 단위테스트 코드 작성이 필수적이어야 한다고 느꼈습니다.
- 실제로 사용한다는 관점으로 바라보고 문제를 해결해야 한다는 관점이 생기게 되었습니다.

</div>
</details>

<details>
<summary>StepMate 만의 달력, 랭킹 리스트 에서 필요한 Custom UI Component 를 구현</summary>
<div markdown="1">

### As-Is

- [랭킹 리스트](https://github.com/step-Mate/Android/tree/main/feature/ranking) 의 중첩 스크롤 요구사항

  - 상단바가 가려질 때 까지 스크롤이 먼저 소비됩니다.
  - 상단바가 모두 가려졌다면, 랭킹리스트의 LazyList 가 스크롤을 소비합니다.
  - 랭킹리스트의 끝에 도달했다면, 랭킹 정보를 더 가져오기 위한 스크롤을 소비합니다.

- [랭킹 리스트](https://github.com/step-Mate/Android/tree/main/feature/ranking) UI Component 요구사항

  - 첫 아이템이 보여지지 않을 때, 스크롤바와 맨위로 이동하기 버튼이 노출됩니다.
  - 스크롤바를 드래그시 해당 위치의 아이템이 있는 곳으로 스크롤 됩니다.
  - 맨위로 이동하기 버튼을 누르면, 랭킹리스트의 첫 아이템으로 스크롤 됩니다.
  - 3초 동안 스크롤이 일어나지 않는다면, 맨위로 이동하기 버튼이 보이지 않습니다.

- [달력](https://github.com/step-Mate/Android/tree/main/feature/home) 화면의 요구사항

  - 년/월/일 단위로 선택할 수 있습니다. 헬스 커넥트 정책에 따라, 권한을 승인 받은 시점으로 부터 30일 전 ~ 오늘까지의 달력을 표시합니다.
    - 연도 선택 달력은 선택 가능한 년도 들을 달력에 버튼으로 표시합니다.
    - 월 선택 달력은 선택 가능한 월 이내의 숫자를 달력에 버튼으로 표시합니다.
    - 일 선택 달력은 해당 월의 일간 달력을 표시합니다.
      - 일간 달력의 기본 형태는 6주 만큼을 표기합니다.
      - 이번달의 일자 범위를 벗어나는 요소들은 "저번달의 마지막 주" 또는 "다음달의 첫주" 일자로 표기합니다.
  - 선택된 년/월/일 단위의 걸음수와 걸음수에 대한 칼로리, 시간을 차트 형태로 노출합니다.
    - 연도 선택시, 해당 연도의 1개월 단위의 정보를 차트에 노출합니다.
    - 월 선택시, 해당 월의 1일 단위의 정보를 차트에 노출합니다.
    - 일 선택시, 해당 일의 24시간 단위의 정보를 차트에 노출합니다.

### Challenge

- **중첩 스크롤**
  - **NestedScrollConnection** 인터페이스를 구현하는 클래스를 만들고, **Modifier#nestedScroll** 의 인자로 넣어 구현하였습니다.
  - 랭킹 리스트의 계층구조에서 **가장 먼저 스크롤을 소비할 수 있는 요소는 LazyList** 입니다.
  - 하지만 [상단바가 있는 레이아웃](https://github.com/step-Mate/Android/blob/main/design/src/main/kotlin/com/stepmate/design/component/systembarhiding/SystemBarHiding.kt)이 **가장 먼저 스크롤을 소비**해야 하므로 **NestedScrollConnection#onPreScroll** 을 이용하여 스크롤을 소비하였습니다.
  - 이후, LazyList 의 첫아이템에 위치하여 **스크롤이 더이상 발생하지 않을 때** 상단바가 다시 보이도록 하기 위해 **NestedScrollConnection#onPostScroll** 을 이용하여 상단바가 나타나는 스크롤을 소비하였습니다.
  - **랭킹리스트의 끝에서 스크롤이 발생하면** LazyList 에서 소비되지 않은 스크롤이 발생하게 되고, LazyList 를 감싸는 레이아웃에서 **NestedScrollConnection#onPostScroll** 로 **랭킹 정보를 더 가져오기 위한 스크롤**을 소비하였습니다.
- [스크롤바](https://github.com/step-Mate/Android/blob/main/design/src/main/kotlin/com/stepmate/design/component/lazyList/VerticalScrollBar.kt)
  - LazyList 의 **item view size** 에 **item 개수**를 **곱하여** 스크롤 바의 위치를 계산하였습니다.
  - 스크롤 바의 Modifier#pointerInput 으로 발생한 **드래그의 양**에서 **item view size 로 나누어** 해당 위치의 **아이템 index**를 구하고 **LazyListState#scrollToItem(index)** 로 이동하도록 구현하였습니다.
- **맨위로 이동하기 버튼**
  - 맨위로 이동하기 버튼을 클릭하면 LazyListState#animateScrollToItem(0) 을 이용하여 첫 아이템으로 스크롤 하였습니다.
  - [코루틴을 활용한 타이머](https://github.com/step-Mate/Android/blob/main/design/src/main/kotlin/com/stepmate/design/component/lazyList/TimeScheduler.kt) 클래스를 이용하여 3초동안 스크롤이 일어나지 않을 때 **Modifier#alpha** 의 인자로 animate 상태값을 조절하여 보이지 않도록 구현하였습니다.
- **달력 화면**
  - 데이터
    - 6주 * 7일 의 달력은 1..42 의 숫자 배열로 구성하고, java.time.zonedDataTime 을 이용하여 날짜를 계산하였습니다.
    - 일간 달력의 계산에는 3가지 요소가 필요합니다.
      - **"이번달 1일의 요일에 대한 dayOfWeek"(일요일 ~ 토요일 에 대해 0~6)**
      - **이번달 말일**
      - **저번달 말일**

    - **첫주**에는 (숫자 배열의 값 + ("저번달 말일" - "이번달 1일의 요일의 dayOfWeek")) 으로 계산합니다.
      - 예) 8월 1일이 목요일(4), 7월의 말일이 31 일 때, (1 + (31 - 4)) = 28, (2 + (31 - 4)) = 29, (3 + (31 - 4)) = 30, (4 + (31 - 4)) = 31 ...
    - **이번달 일자**는 (숫자 배열의 값 - "이번달 1일의 요일에 대한 dayOfWeek") 으로 계산합니다.
      - 예) (5 - 4) = 1, (6 - 4) = 2, (7 - 4) = 3
    - **이번달 말일 이후**는 (숫자 배열의 값 - ("이번달 말일" + "이번달 1일의 요일에 대한 dayOfWeek")) 으로 계산합니다.
      - 예) 8월의 말일이 31 일 때, (36 - (31 + 4)) = 1, (37 - (31 + 4)) = 2, (38 - (31 + 4)) = 3, (39 - (31 + 4)) = 4 ...
  - 달력화면을 만들기 위해 **Layout()** 컴포저블 함수를 이용하여 **view의 크기 측정**과 **위치를 결정**시켜 구현하였습니다.

### To-Be

- Compose 의 **Layout 단계를 구현**하여 **Custom UI Component 를 만들수 있는 방법**을 알게 되었습니다.
- **사용자 상호작용**이 **view tree 계층구조**에서 **어떤 방식으로 수행**되는지 알게 되었습니다.

</div>
</details>

<details>
<summary>클래스 설계에 디자인패턴들을 적용</summary>
<div markdown="1">

### As-Is

- 헬스 케어 데이터
  - StepMate 에서 제공하는 헬스케어 데이터는 걸음수 와 걸음수를 기반으로 계산된 칼로리와 시간 이다.
  - 추후 심박수, 혈당량 과 같은 다른 헬스 케어 데이터도 추가할 예정이다.
  - SOLID 원칙중 OCP(개방-폐쇄 원칙)에 따라 지원하는 헬스케어 데이터가 늘어나도 기존의 공통 UI Component 와 비즈니스 로직은 그대로 재사용 될 수 있어야 한다.
  - 이를 위해 SOLID 원칙중 LSP(리스코프 치환 원칙) 의 근거인 객체지향 프로그래밍의 다형성에 따라 클래스 설계는 상속과 구현의 관계를 갖도록 해야 하며, SOLID 원칙중 DIP(의존성 역전 원칙)에 따라 UI Component 와 비즈니스 로직에서의 참조는 상위수준을 의존해야 한다.
  - 위의 조건을 만족시키기 위해 [HealthCare](https://github.com/step-Mate/Android/blob/develop/feature/home/src/main/kotlin/com/stepmate/home/screen/home/state/HealthCare.kt) 를 의존하도록 구현했지만 구체적(걸음수, 심박수 등) 클래스들은 객체의 생성에 필요한 생성 로직의 복잡도가 커지는 문제가 발생
- 미션 데이터
  - 처음에는 미션정보가 (걸음수:50) 또는 (칼로리:100) 과 같이 하나의 미션을 제공하려 했지만,
  - 추후, (걸음수:100 & 칼로리:200) 과 같이 여러개의 미션이 합쳐진 하나의 미션을 제공해야하는 요구사항이 발생

### Challenge

- 헬스 케어 정보 클래스 설계에 [Factory Pattern](https://github.com/step-Mate/Android/blob/develop/feature/home/src/main/kotlin/com/stepmate/home/screen/home/state/Step.kt) 적용하였습니다.
  - 모든 구체적 클래스([걸음수](https://github.com/step-Mate/Android/blob/develop/feature/home/src/main/kotlin/com/stepmate/home/screen/home/state/Step.kt) , [심박수](https://github.com/step-Mate/Android/blob/develop/feature/home/src/main/kotlin/com/stepmate/home/screen/home/state/HeartRate.kt))들의 **생성에 필요한 정보가 다르고**
  - 생성의 **전후처리의 로직이 모두 달라서** 복잡도가 커졌기 때문에 생성 로직을 **팩토리 패턴**으로 분리하여 해결하였습니다.
- 미션 정보 클래스 설계에 [Composite Pattern](https://github.com/step-Mate/Android/tree/develop/domain/src/main/kotlin/com/stepmate/domain/model/mission) 적용하였습니다.
  - 각각의 하나의 미션들과 미션들이 합쳐진 복합 미션 모두가 동등하게 **하나의 미션으로 취급**되어야 하기 때문에 **컴포지트 패턴**을 적용하여 해결하였습니다.

### To-Be

- **SOLID 원칙**과 **OOP**, 그리고 구체적 상황에서 이를 실현하는 하나의 방법론인 **디자인 패턴**들을 학습하며 **클래스 설계에 관한 지식을 늘리고 견해를 쌓게**되었습니다.
- 제가 제일 좋아하는 원칙은 **OCP** 입니다. 
  - 사용자가 직접 다루는 애플리케이션 개발에서 가장 큰 비용은 **인적비용**이고
  - 이는 **시간** 과 직결된다고 생각합니다. 
  - OCP 원칙을 잘 따르도록 설계한다면, 기존의 핵심 비즈니스 로직이나 공통 UI Component 들을 **그대로 재사용**할 수 있기 때문에 코드 **유지보수에 드는 시간적 비용이 획기적으로 단축**될 수 있을 것이라 느꼈습니다.

</div>
</details>

# Member

> **황진호 ([jowunnal](https://github.com/jowunnal "github link"))** 

> **공경일 ([kyungil9](https://github.com/kyungil9 "github link"))** 

<br>

| 황진호 | 공경일 |
| ----- | ----- |
| 걸음수 수집 & 홈 화면, 랭킹 화면 | 미션 화면, 내정보 화면 |
