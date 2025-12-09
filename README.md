# Go Game – Iteracja 1 (klient–serwer)

Projekt zaliczeniowy z laboratorium – uproszczona gra **Go** w architekturze **klient–serwer**.

* logika gry (plansza, bicia, zakaz samobójstwa) po stronie serwera,
* dwaj klienci łączą się do serwera i grają przeciwko sobie,
* interfejs tekstowy (terminal) – rysowanie planszy ascii,
* projekt zrealizowany w Javie 17 z użyciem **Mavena**.

---

## 1. Funkcjonalność (Iteracja 1)

Zaimplementowane są zasady Go wymagane w iteracji 1:

* rozgrywka dwóch graczy: **BLACK** i **WHITE**,
* plansza kwadratowa (domyślnie 9×9),
* kolejność ruchów: **BLACK zaczyna**, potem naprzemiennie,
* legalny ruch:

  * kamień stawiany na puste pole,
  * bicie całych grup przeciwnika po utracie oddechów,
  * **zakaz samobójstwa** (chyba że ruch bije kamienie przeciwnika),
* możliwe akcje gracza:

  * `MOVE x y` – ruch na podane współrzędne,
  * `PASS` – pas,
  * `RESIGN` – rezygnacja (przyznanie się do przegranej),
* koniec gry:

  * jeden z graczy wpisuje `RESIGN` → drugi wygrywa,
  * (logika dwóch pasów jest przygotowana w `Game`, ale na razie nie liczymy punktów).

---

## 2. Wymagania

* Java 17+
* Maven 3.x
* dostęp do konsoli / terminala (Windows / Linux / WSL / macOS)

---

## 3. Budowanie projektu

W katalogu z `pom.xml`:

```bash
mvn clean compile
```

Po udanym kompilowaniu będzie dostępny katalog `target/classes` z plikami `.class`.

---

## 4. Uruchamianie – serwer i dwaj klienci

### 4.1. Uruchom serwer

W terminalu:

```bash
java -cp target/classes pl.edu.go.server.GameServer
```

Serwer:

* nasłuchuje na porcie **5000**,
* tworzy planszę 9×9,
* czeka na dwóch graczy.

W logu serwera zobaczysz m.in.:

```text
Server listening on port 5000
First player connected (BLACK)
Second player connected (WHITE)
Game started. Waiting for moves...
```

### 4.2. Uruchom dwóch klientów (w dwóch osobnych terminalach)

Każdy klient:

```bash
java -cp target/classes pl.edu.go.client.cli.CliClient
```

Po połączeniu zobaczysz coś w stylu:

```text
Connected to localhost:5000
INFO Connected as BLACK
Commands: MOVE x y | PASS | RESIGN  (or: exit)
WELCOME BLACK
INFO Game started. BLACK moves first.

Current board:
    0 1 2 3 4 5 6 7 8
 0  . . . . . . . . .
 1  . . . . . . . . .
 ...
 8  . . . . . . . . .

TURN BLACK
```

Pierwszy zalogowany klient gra czarnymi (**BLACK**), drugi – białymi (**WHITE**).

---

## 5. Sterowanie z konsoli

W oknie klienta wpisujesz komendy:

* **ruch**:

  ```text
  MOVE x y
  ```

  gdzie `x` i `y` to współrzędne od `0` do `size-1`, np.:

  ```text
  MOVE 3 4
  ```

* **pas**:

  ```text
  PASS
  ```

* **rezygnacja** (przegrana z własnej woli):

  ```text
  RESIGN
  ```

* **wyjście z klienta** (bez wysyłania komendy do serwera):

  ```text
  exit
  ```

  lub

  ```text
  quit
  ```

Po każdym poprawnym ruchu:

* serwer wysyła nowy stan planszy (`BOARD / ROW / END_BOARD`),
* klient rysuje ją w terminalu jako grid z symbolami:

  * `●` – kamień czarny,
  * `○` – kamień biały,
  * `.` – puste pole.

Po zakończeniu gry (np. po `RESIGN`) serwer wysyła:

```text
END BLACK resign
```

lub

```text
END WHITE resign
```

Klient wyświetla ten komunikat i **automatycznie kończy działanie**.

---

## 6. Protokół tekstowy klient–serwer

Komunikacja opiera się na prostym protokole tekstowym (jedna linia = jedna wiadomość).

### 6.1. Komendy od klienta do serwera

* `MOVE x y` – próba zagrania kamienia na `(x,y)`,
* `PASS` – pas,
* `RESIGN` – rezygnacja.

Serwer rozpoznaje komendy w `GameSession` przy pomocy **TextCommandFactory** i **Command**:

```java
GameCommand command = commandFactory.fromNetworkMessage(trimmed, from.getColor());
command.execute(game);
```

### 6.2. Odpowiedzi od serwera do klienta

* `INFO <tekst>` – komunikaty informacyjne (np. start gry),
* `WELCOME BLACK|WHITE` – przypisanie koloru klientowi,
* `TURN BLACK|WHITE` – informacja, kto ma aktualnie ruch,
* `ERROR <opis>` – błąd (np. nielegalny ruch),
* `END <WINNER> <reason>` – koniec gry,
* **opis planszy:**

  ```text
  BOARD <size>
  ROW <wiersz0>
  ROW <wiersz1>
  ...
  ROW <wierszN-1>
  END_BOARD
  ```

  gdzie:

  * `<size>` – rozmiar planszy (np. 9),
  * `<wiersz>` – ciąg znaków `'.'`, `'X'`, `'O'`:

    * `.` – puste pole,
    * `X` – kamień czarny,
    * `O` – kamień biały.

Klient **parsuje** (`BOARD / ROW / END_BOARD`) i rysuje planszę w czytelnej formie w metodzie `displayBoard`.

---

## 7. Struktura pakietów

Kod jest podzielony na pakiety zgodnie z odpowiedzialnościami:

```text
pl.edu.go.board
    Board           // logika planszy: oddechy, grupy, bicie, zakaz samobójstwa
    BoardFactory    // fabryka tworząca plansze o zadanym rozmiarze

pl.edu.go.model
    Stone           // pojedynczy kamień
    StoneGroup      // grupa kamieni (łańcuch)

pl.edu.go.move
    Move            // pojedynczy ruch (kolor + x,y)
    MoveAdapter     // adapter z notacji użytkownika na współrzędne
    MoveFactory     // fabryka tworząca obiekty Move

pl.edu.go.game
    Game            // logika wysokiego poziomu: aktualny gracz, pass, resign
    GameObserver    // interfejs obserwatora gry (Observer)
    GameResult      // prosty wynik gry (zwycięzca, powód)
    PlayerColor     // enum BLACK/WHITE + mapowanie na Board.BLACK/WHITE

pl.edu.go.command
    GameCommand         // interfejs wzorca Command
    PlaceStoneCommand   // komenda: postaw kamień
    PassCommand         // komenda: pas
    ResignCommand       // komenda: rezygnacja
    TextCommandFactory  // fabryka parsująca tekst protokołu na GameCommand

pl.edu.go.server
    GameServer      // punkt startowy serwera, akceptuje dwóch klientów
    GameSession     // jedna sesja gry: łączy Game, Command i klienta
    ClientHandler   // obsługa pojedynczego klienta (wątek, socket)

pl.edu.go.client.cli
    CliClient       // klient konsolowy: czyta komendy z klawiatury,
                    // parsuje BOARD/ROW/END_BOARD i rysuje planszę
```

---

## 8. Wzorce projektowe

W projekcie wykorzystano kilka wzorców projektowych:

### 8.1. Composite

* **Gdzie:** `Stone` + `StoneGroup` w pakiecie `pl.edu.go.model`.
* **Opis:** Kamień (`Stone`) jest liściem, `StoneGroup` jest kompozytem reprezentującym łańcuch kamieni.
  `Board` operuje na grupach (`StoneGroup`) przy liczeniu oddechów i usuwaniu całych łańcuchów jednym wywołaniem.

### 8.2. Adapter

* **Gdzie:** `MoveAdapter` w pakiecie `pl.edu.go.move`.
* **Opis:** Tłumaczy wejście użytkownika (np. notacja tekstowa) na współrzędne `(x, y)` używane przez `Board`.
  Dzięki temu `Board` pozostaje czystą logiką bez znajomości formatu wejściowego.

### 8.3. Factory Method

* **Gdzie:**

  * `BoardFactory` (tworzenie planszy o zadanym rozmiarze),
  * `MoveFactory` (tworzenie obiektów `Move`),
  * `TextCommandFactory` (tworzenie `GameCommand` na podstawie linii tekstu).
* **Opis:** Konkretny sposób tworzenia obiektów jest ukryty w fabryce; reszta kodu używa tylko interfejsu (`Board`, `Move`, `GameCommand`), co ułatwia testy i późniejsze rozszerzenia.

### 8.4. Command

* **Gdzie:** pakiet `pl.edu.go.command`.
* **Opis:** Każda akcja gracza (ruch, pas, rezygnacja) jest reprezentowana jako obiekt:

  * `GameCommand` – interfejs,
  * `PlaceStoneCommand`, `PassCommand`, `ResignCommand` – konkretne komendy.

  Serwer:

  ```java
  GameCommand command = commandFactory.fromNetworkMessage(trimmed, from.getColor());
  command.execute(game);
  ```

  Dzięki temu logika wykonania ruchu jest oddzielona od logiki sieci / parsowania.

### 8.5. Observer

* **Gdzie:** `Game`, `GameObserver`, `GameSession` w pakiecie `pl.edu.go.game` / `pl.edu.go.server`.
* **Opis:** `Game` pełni rolę **subject** – ma listę obserwatorów (`GameObserver`) i powiadamia ich o:

  * zmianie planszy (`onBoardChanged`),
  * zmianie gracza (`onPlayerToMoveChanged`),
  * zakończeniu gry (`onGameEnded`).

  `GameSession` implementuje `GameObserver` i, po każdej zmianie, wysyła odpowiednie komunikaty do klientów (`BOARD`, `TURN`, `END`).

### 8.6. MVC / MVP (planowane dla GUI)

* W projekcie przewidziano dodatkowy moduł `client.gui` (GUI w osobnym oknie), który będzie korzystał z:

  * **Modelu** (`Game` / `GameModel`),
  * **View** (`BoardView`),
  * **Controller/Presenter** (`GameController` – obsługa kliknięć, komunikacja z serwerem).

  W Iteracji 1 skupiamy się na interfejsie konsolowym, ale architektura już przewiduje GUI.

---

## 9. Ograniczenia i uproszczenia

* Serwer obsługuje jedną grę naraz i **dokładnie dwóch klientów**:

  * po rozłączeniu klienta serwer należy uruchomić ponownie przed kolejną partią,
* nie liczymy jeszcze punktów – gra kończy się głównie przez `RESIGN`,
* brak zaawansowanych reguł (ko, superko etc.) – zgodnie z wymaganiami Iteracji 1.

---

## 10. Uruchamianie w skrócie

1. Skompiluj:

   ```bash
   mvn clean compile
   ```

2. Serwer:

   ```bash
   java -cp target/classes pl.edu.go.server.GameServer
   ```

3. Klient 1 (BLACK):

   ```bash
   java -cp target/classes pl.edu.go.client.cli.CliClient
   ```

4. Klient 2 (WHITE):

   ```bash
   java -cp target/classes pl.edu.go.client.cli.CliClient
   ```

5. Graj komendami: `MOVE x y`, `PASS`, `RESIGN`.
