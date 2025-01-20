# JAVA HW - Block Breaker Game
> JAVA Programming 과제5 : 블럭 부시기 게임

## ✍️ 개요
1. [Stacks](#1-stacks)
2. [요구 조건](#2-요구-조건)
3. [화면 별 기능 명세](#3-화면-별-기능-명세)
    - [GameTitleScreen](#gametitlescreen)
    - [GameScreen](#gamescreen)
    - [GameOverScreen](#gameoverscreen)
4. [디자인 요소](#4-디자인-요소)
5. [배경음악 및 효과음](#5-배경음악-및-효과음)
6. [어려웠던 점](#6-어려웠던-점)

&nbsp;

## 1. Stacks
### Environment
![](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)
![](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)

### Development
![](https://img.shields.io/badge/JAVA-007396?style=flat-square&logo=openjdk&logoColor=white)

### Design
![](https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=figma&logoColor=white)

&nbsp;

## 2. 요구 조건
1. 한 파일 안에 통합해서 코드를 작성할 것. ( 채점 이슈 ㅜ)
2. 자바의 Swing 및 Java2D 등의 라이브러리를 이용하여 만들것
3. Thread를 이용하여 애니메이션 및 조작이 가능하도록 할 것
4. 최대한 객체지향적인 방식으로 설계해 보도록 할 것
5. 최소 1개 이상의 추상클래스 및 2개 이상의 자식클래스를 디자인 할 것
6. 게임은 타이틀화면/게임화면/게임오버 화면으로 구성되며 각각 다른 클래스로 만들수 있도록 해 볼 것
7. 이미지나, 사운드를 활용하여 보다 재미있는 게임을 만들어 볼 것

&nbsp;

## 3. 화면 별 기능 명세
### GameTitleScreen
<img src="https://github.com/user-attachments/assets/45b212cd-0791-419a-9eed-eeccc1391e5a" width="500px" height="500px" alt="GameTitleScreen"></img><br/>

- `spacebar`를 누르면 게임 화면으로 이동
- 글자 중앙 배치
- 배경화면과 글자 그라데이션 설정
- 눈 내리는 애니메이션 구현
- "Press Spacebar to play!" 안내문구 깜박이게 설정

### GameScreen
<img src="https://github.com/user-attachments/assets/0d740064-7f71-4799-aaf4-3a74bd6a53ad" width="500px" height="500px" alt="GameTitleScreen"></img><br/>

- `KEY_LEFT`, `KEY_RIGHT`를 눌러서 paddle 위치를 조절하여 공을 튕겨 블럭을 부술 수 있음.
- 회색 벽돌 : 3번 부숴야 깨짐.
- 밝은 벽돌 : 부수면 공이 2개로 갈라짐.
- 블럭을 모두 깨면 다음 단계로 이동.
- 공이 모두 사라지면 `GameOverScreen`으로 이동.

### GameOverScreen
<img src="https://github.com/user-attachments/assets/94132900-b979-4beb-ba96-521ddf8c0914" width="500px" height="500px" alt="GameTitleScreen"></img><br/>

- 최고 점수와 현재 점수를 표시.
- `spacebar`를 누르면 `GameTitleScreen`으로 돌아감.

&nbsp;

## 4. 디자인 요소
- text를 두 개 겹쳐서 입체감을 줬다. 

## 5. 배경음악 및 효과음

## 6. 어려웠던 점
- 사실 복잡한 기능이 있는 프로젝트는 아니라서 기능 구현 단에서 크게 어려움은 없었음.
- 생각보다 효과음 부분에서 시간을 오래썼음. 적적한 타이밍에 적절한 효과음과 배경을 넣는 게 게임의 퀄리티를 확 올려준다는 것을 알게 됨.
- 

