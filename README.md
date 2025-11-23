## 🏎️ F1 Betting & Race Simulation

Spring Boot 기반으로 제작된 F1 레이스 베팅 시뮬레이션입니다.   
한 경기는 총 5개의 라운드로 구성되며, 각 라운드마다 10개 차량 중 무작위로 3대를 선택하여 경기가 진행됩니다.

라운드는 매번 서로 다른 날씨 환경을 가지며,   
차량에 저장된 고유 스탯(속도, 가속, 코너링, 내구도, 운 등)에 랜덤 변동 값과 날씨 보정치가 적용되어 최종 승자가 결정됩니다.

경기 진행 정보는 WebSocket(STOMP) 를 통해 실시간으로 전송되며, 사용자는 각 라운드에서 제공된 차량 중 하나를 선택하여 베팅을 진행할 수 있습니다.
베팅한 차량이 승리하면, 차량의 배당(odds) 에 따라 수익을 획득하게 됩니다.

<br>

----

### 🎥 시연 영상 (Demo Video)

![시연영상](https://github.com/user-attachments/assets/e3bcb1fa-2690-4c8f-86b5-fd6671a42386)

<br>

---

### ✨ 주요 기능 (Features)

- **5라운드 레이스 시뮬레이션**  
  라운드마다 3대의 차량 랜덤 선정 후 진행

- **실시간 WebSocket 중계**  
  tick 단위 이동 거리, 속력, 순위 변화가 실시간으로 전송

- **날씨 시스템 적용**  
  SUNNY, CLOUDY, RAINY 등 6종 날씨에 따른 보정 적용

- **랜덤 변동치 기반 엔진**  
  acceleration, general variation, luck, malfunction 등이 매 tick에 반영

- **베팅 시스템 지원**  
  선택 차량이 승리하면 배당 기반으로 수익을 획득

- **정산(PAYOUT) 자동 처리**  
  라운드 종료 후 즉시 수익 계산 및 DB 반영
  
<br>

---

### ⏰ 라운드 진행 흐름
```
READY → BETTING → LOCK → RUNNING → FINISH → PAYOUT
```

<br>


---

### 🚗 차량 능력치 

각 차량은 다음과 같은 고유 스탯을 가지며, 레이스 엔진은 이 값을 기반으로 매 tick마다 거리와 속도를 계산합니다.

| 스탯 | 설명 |
|------|------|
| **speed** | 속도, 기본적인 거리 증가량에 기여함. |
| **acceleration** | 가속 성능, tick마다 속도를 얼마나 빠르게 올리는지 결정. |
| **cornering** | 코너 구간 성능, 비·안개 등 날씨 변화에 크게 영향 받음. |
| **durability** | 내구성, 낮을수록 고장 확률 상승. |
| **luck** | 운 요소, 특정 순간에 성능이 급상승하는 부스트 발생 가능. |
| **malfunction_rate** | 고장 확률, durability와 함께 고장 발생 여부 판단. |

<br>

---

### 🌤️ 날씨 시스템 

## Weather Table

| 날씨 | 보정계수 | 중계 멘트 |
|------|----------|--------------------------------------------------------------|
| ☀️ **SUNNY** | **1.2** | 트랙 상태가 최상이며, 빠른 기록이 기대됩니다. |
| ☁️ **CLOUDY** | **1.1** | 기온이 낮아 엔진 효율이 안정적입니다. |
| 🌧️ **RAINY** | **0.9** | 노면이 미끄러워 코너링과 제동이 불안정해집니다. |
| ⛈️ **STORM** | **0.8** | 강한 비바람으로 매우 위험한 레이스 환경이 됩니다. |
| 🍃 **WINDY** | **1.1** | 바람이 강해 직선에서 흔들릴 수 있습니다. |
| ☃️ **SNOWY** | **0.7** | 눈으로 인해 제어가 어렵고 전체 성능이 크게 감소합니다. |

<br>

---

## 🏁 Tick 계산 방식

MovementEngine은 매 tick마다 아래 요소를 모두 곱해 차량의 이동 거리(distance)를 계산합니다.

```
계산 공식:
speed × accelerationEffect × weatherEffect × durabilityEffect × luck × malfunction × variation × SCALE
```
<br>

---


## 📡 API Endpoints

### 🔐 Auth
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/auth/signup` | 회원가입 |
| POST | `/auth/login` | 로그인 |

### 💸 Betting
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/bet/place` | 특정 라운드 차량에 베팅 |

###  🏁 Race
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/race/start` | 새 Race 생성 |
| POST | `/race/next` | 다음 라운드 시작 요청 |

### 📡 WebSocket Topics
| Topic | 설명 |
|--------|------|
| `/topic/user/{userId}/race/{round}` | tick 업데이트·날씨·우승자 등 기본 레이스 데이터 |
| `/topic/user/{userId}/race/{round}/cars/group` | 라운드 시작 시 선택된 차량 3대 정보 |
| `/topic/user/{userId}/race/{round}/weather` | 해당 라운드의 날씨 정보 아이콘/멘트 |
| `/topic/user/{userId}/race/{round}/cars/odd` | 차량 배당률(odds) 정보 |
| `/topic/user/{userId}/race/{round}` | 레이스 종료 정보 |



