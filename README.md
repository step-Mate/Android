<a href="https://play.google.com/store/apps/details?id=com.jinproject.twomillustratedbook">
	<img src="https://img.shields.io/badge/PlayStore-v1.0.1-4285F4?style=for-the-badge&logo=googleplay&logoColor=white&link=https://play.google.com/store/apps/details?id=com.jinproject.twomillustratedbook" />
</a>

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Gradle](https://img.shields.io/badge/gradle-8.1.0-green.svg)](https://gradle.org/)
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

 - Kotlin
 - Compose
 - Compose - Navigation ( Single Activity )
 - Kotlin.Coroutines.Flow
 - Room
 - DataStore(proto)
 - AlarmManager
 - Service
 - Hilt
 - Retrofit2 & Okhttp3
 - HealthConnect

# Point

- Material3 디자인 가이드라인 및 팀 간의 약속에 따라 color, theme 을 분류 및 다크테마에 대응하였습니다.
- Compose 의 Composition-Layout-Drawing 3단계를 이해한 뒤 달력, progress indicator 와 같은 custom 한 layout을 만들었 습니다. 중첩 스크롤, 드래그와 같은 상호작용이 viewTree 내에서 우선순위를 어떻게 가지는지 이해하여 드래그에 따라 상단바가 사라지는 design component 를 만들었으며, custom 한 pushRefreshState 를 만들어 위로 드래그시 페이징으로 나뉘어진 랭킹리스트의 다음 페이지를 추가로 가져오는 커스텀 UI를 구현하였습니다.
- 앱 실행시 androidx.core.splashscreen api로 splash 노출 후 제거 타이밍을 앱 실행에 필요한 권한들의 승인 여부 완료까지 지연시킨뒤, 권한이 필요하면 권한 화면을 노출하고 그렇지않으면 홈화면으로 전환하는 권한 관리 플로우를 구현하였습니다.
- 포그라운드 서비스의 특성을 활용하여 실시간으로 걸음수를 수집 및 필터링하여 헬스커넥트와 서버로 저장하고 가져오는 알고리즘을 구현하였습니다.
- Compose 에서 순수함수 특성(멱등성) 과 SideEffect 를 다루기 위한 api 들을 이해한 뒤, 이를 이용하여 Compose-State 를 최적화 함으로써 불필요한 recomposition 을 방지하도록 구현하였습니다.

# Member

> **황진호 ([jowunnal](https://github.com/jowunnal "github link"))** 

> **공경일 ([kyungil9](https://github.com/kyungil9 "github link"))** 

<br>

| 황진호 | 공경일 |
| ----- | ----- |
| 걸음수 수집 & 홈 화면, 랭킹 화면 | 미션 화면, 내정보 화면 |
