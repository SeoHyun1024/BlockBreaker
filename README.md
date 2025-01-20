# JAVA HW - Block Breaker Game
> JAVA Programming 과제5 : 블럭 부시기 게임
> <br>개발기간 : 2024.12.19~2024.12.23

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
   - [트리 그리기](#트리-그리기)
   - [별 찍기](#별-찍기)

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
- 특수 블럭들은 전체 블럭 갯수의 30% 이하로 나타나게 설정함.
- 블럭을 모두 깨면 다음 단계로 이동.
- 공이 모두 사라지면 `GameOverScreen`으로 이동.

### GameOverScreen
<img src="https://github.com/user-attachments/assets/94132900-b979-4beb-ba96-521ddf8c0914" width="500px" height="500px" alt="GameTitleScreen"></img><br/>

- 최고 점수와 현재 점수를 표시.
- `spacebar`를 누르면 `GameTitleScreen`으로 돌아감.

&nbsp;

## 4. 디자인 요소
- text를 두 개 겹쳐서 입체감을 줬다.
- 블럭의 border에 그라데이션 효과를 주어서 벽돌처럼 입체감이 들도록 하였다. 나름 벽돌처럼 구현된 것 같다.
- 제작 시기가 크리스마스 직전이라 컨셉을 크리스마스로 잡고 굴뚝 느낌으로 구성했다. `GameTitleScreen`에서 캐롤을 틀고 싶었는데 원하는 느낌을 찾기 어려워 오락실 느낌의 배경음악으로 절충했다.

## 5. 배경음악 및 효과음
- 게임 효과음 사이트에서 mp3 파일을 다운받아 `javax.sound` 라이브러리를 사용했는데 wav 파일만 지원하였기 때문에 wav 파일로 변환 후 넣어주었다. 
- 특수 블럭에 부딪힐 때, paddle에 공이 부딪힐 때, 게임 오버될 때 효과음을 넣어주었다.
- 오디오가 너무 겹치면 화면이 끊기는 이슈가 발생해 `SoundManager.java` 파일을 만들어 소리를 먼저 로드 해놓고 출력하는 방식으로 수정하였다.
  ```java
      private static synchronized void loadSound(String fileName) {
        try {
            URL url = SoundManager.class.getClassLoader().getResource(fileName);
            if (url == null) {
                System.err.println("Could not find audio file: " + fileName);
                return;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clips.put(fileName, clip);
        } catch (Exception e) {
            System.err.println("Error loading sound " + fileName + ": " + e.getMessage());
        }
    }
  ```

## 6. 어려웠던 점
- 사실 복잡한 기능이 있는 프로젝트는 아니라서 기능 구현 단에서 크게 어려움은 없었음.
- 생각보다 효과음 부분에서 시간을 오래썼음. 적적한 타이밍에 적절한 효과음과 배경을 넣는 게 게임의 퀄리티를 확 올려준다는 것을 알게 됨.
- 오브젝트 크기 조절이나 위치 조절이 조금 힘들었던 거 같다.

### 트리 그리기
```java
   private  void drawTree(Graphics2D g2, int x, int y, int treeHeightOffset){
        int triangleHeight = 48;
        int triangleWidth = 70;

        // 삼각형 그리기
        for (int i = 0; i < 4; i++){
            int[] xPoints = {x, x - triangleWidth /2 - i*i*5, x + triangleWidth / 2 + i*i*5};
            int[] yPoints = {y + (i * triangleHeight ) - 40 - treeHeightOffset, y + (i * triangleHeight + triangleHeight) - treeHeightOffset, y + (i * triangleHeight +triangleHeight - treeHeightOffset )};
            Polygon triangle = new Polygon(xPoints, yPoints, xPoints.length);
            g2.setColor(Color.WHITE);
            g2.fillPolygon(triangle);
        }

        // 나무 기둥 그리기
        int trunkWidth = 20;
        int trunkHeight = 60;
        g2.setColor(Color.WHITE);
        g2.fillRect(x - trunkWidth / 2, y + triangleHeight*4 - 60, trunkWidth, trunkHeight + 40);
    }

    private void drawTreeRow(Graphics2D g2, int panelWidth, int y){
        int numTrees = 10;
        int treeWidth = 60;
        int[] spacing = {25, 25, 20, 30, 20, 30, 20, 25, 20, 25 };
        int[] heightOffset = {5, 40, 10, 30, 0, 35, 0, 55, 10, 20};


        for (int i = 0; i < numTrees; i++) {
            int totalWidth = numTrees * treeWidth + (numTrees - 1) * spacing[i];
            int startX = (panelWidth - totalWidth) / 2;
            int treeX = startX + i * (treeWidth + spacing[i]);
            drawTree(g2, treeX, y, heightOffset[i]);
        }
    }
```
- 나무의 간격과 위치를 배열로 저장해서 paint 함.
- 나무가 너무 일정하게 생기면 이상해서 삼각형의 높이를 다르게 설정했다.

### 별 찍기
```java
class Star {
    private int x, y, speed;

    public Star(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move() {
        y += speed;
        if (y > 600) { // Reset position if it moves off-screen
            y = 0;
            x = new Random().nextInt(800);
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(240, 235, 192));
        g.fillOval(x, y, 5, 5);
    }
}

    private void startStarAnimation() {
        Timer timer = new Timer(50, e -> {
            for (Star star : stars) {
                star.move();
            }
            repaint();
        });
        timer.start();
    }

```
- `Star` class를 만들어 랜덤으로 생성되게 하였고, y값을 조절하여 점점 떨어지는 효과를 주었다.
- 애니메이션은 `Timer` 를 이용해 특정 시간 마다 repaint 하여 구현하였다.
