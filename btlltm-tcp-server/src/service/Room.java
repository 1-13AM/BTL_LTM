package service;

import controller.UserController;
import helper.CountDownTimer;
import helper.CustumDateTimeFormatter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import model.UserModel;
import run.ServerRun;
import java.util.Random;

class Product {
    String name;
    int minPrice;
    int maxPrice;

    public Product(String name, int minPrice, int maxPrice) {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}

public class Room {
    String id;
    String time = "00:00";
    Client client1 = null, client2 = null;
    ArrayList<Client> clients = new ArrayList<>();
    
    boolean gameStarted = false;
    CountDownTimer matchTimer;
    CountDownTimer matchTimerRound;
    CountDownTimer waitingTimer;
    
    String resultClient1;
    String resultClient2;
    
    String playAgainC1;
    String playAgainC2;
    String waitingTime= "00:00";

    public LocalDateTime startedTime;
    
    private int currentRound = 0;
    private final int maxRounds = 10;
    private String currentProduct;
    private float currentPrice;
    private float player1Guess;
    private float player2Guess;
    private float player1Score = 0;
    private float player2Score = 0;
    
    public Room(String id) {
        // room id
        this.id = id;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        gameStarted = true;
        
        startNewRound();
    }
    
    private void startNewRound() {
        currentRound++;
        if (currentRound <= maxRounds) {
            getRandomProduct();
            broadcast("NEW_ROUND;" + currentProduct + ";" + currentPrice);
            resetRoundData();
            matchTimer = new CountDownTimer(10);
            matchTimer.setTimerCallBack(
                null,
                (Callable) () -> {
                    time = "" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick());
                    System.out.println(time);
                    if (time.equals("00:00")) {
                        endRound();
                    }
                    return null;
                },
                1
            );
        } else {
            endGame();
        }
    }
    
    private void resetRoundData() {
        player1Guess = 0;
        player2Guess = 0;
        resultClient1 = null;
        resultClient2 = null;
    }
    
    public void handleGuess(Client client, float guess) {
        if (client == client1) {
            player1Guess = guess;
            resultClient1 = "SUBMITTED";
        } else {
            player2Guess = guess;
            resultClient2 = "SUBMITTED";
        }

        if (resultClient1 != null && resultClient2 != null) {
            endRound();
        }
    }
    
    public void calculateRoundResult() {
        float roundScore1 = 0;
        float roundScore2 = 0;
        String winner = "";
        if (player1Guess > currentPrice && player2Guess > currentPrice) {
            if (player1Guess < player2Guess) roundScore1 = 1;
            else if (player2Guess < player1Guess) roundScore2 = 1;
            else {
                roundScore1 = 0.5f;
                roundScore2 = 0.5f;
            }
        } else if (player1Guess <= currentPrice && player2Guess <= currentPrice) {
            if (player1Guess > player2Guess) roundScore1 = 1;
            else if (player2Guess > player1Guess) roundScore2 = 1;
            else {
                roundScore1 = 0.5f;
                roundScore2 = 0.5f;
            }
        } else if (player1Guess <= currentPrice) {
            roundScore1 = 1;
        } else {
            roundScore2 = 1;
        }

        player1Score += roundScore1;
        player2Score += roundScore2;
        
        if (roundScore1  == 1){
            winner = client1.getLoginUser();
        }
        else if (roundScore2 == 1) {
            winner = client2.getLoginUser();
        }
        else {
            winner = "DRAW";
        }
        broadcast("ROUND_RESULT;" + winner + ";" + currentPrice + ";" + client1.getLoginUser() + ";" + client2.getLoginUser() + ";" + player1Guess + ";" + player2Guess + ";" + player1Score + ";" + player2Score);
    }
    
    private void endRound() {
        matchTimer.pause();
        calculateRoundResult();
        matchTimerRound = new CountDownTimer(5);
        matchTimerRound.setTimerCallBack(
            null,
            (Callable) () -> {
                time = "" + CustumDateTimeFormatter.secondsToMinutes(matchTimerRound.getCurrentTick());
                System.out.println(time);
                if (time.equals("00:00")) {
                    startNewRound();
                    matchTimerRound.pause();
                }
                return null;
            },
            1
        );  
    }
    
    private void endGame() {
        String winner = determineWinner();
        String loser = null;
        // Update scores in database first
        try {
            if (winner.equals("DRAW")) {
                draw();
                loser = "DRAW";
            } else if (winner.equals(client1.getLoginUser())) {
                client1Win(0);
                loser = client2.getLoginUser();
            } else {
                client2Win(0);
                loser = client1.getLoginUser();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Then broadcast game end
        // broadcast("GAME_END;" + winner + ";" + Math.max(player1Score, player2Score));
        broadcast("GAME_END;" + winner + ";" + loser + ";" + Math.max(player1Score, player2Score) + ";" + Math.min(player1Score, player2Score));
        // Reset game state
        gameStarted = false;
        currentRound = 0;
        player1Score = 0;
        player2Score = 0;
        
        // Clear client game states
        client1.setJoinedRoom(null);
        client2.setJoinedRoom(null);
        
        // Remove room from server
        ServerRun.roomManager.remove(this);
    }
    
    private String determineWinner() {
        if (player1Score > player2Score) {
            return client1.getLoginUser();
        } else if (player2Score > player1Score) {
            return client2.getLoginUser();
        } else {
            return "DRAW";
        }
    }
    
    private void updateUserScores(String winner) {
        try {
            if (winner.equals("DRAW")) {
                draw();
            } else if (winner.equals(client1.getLoginUser())) {
                client1Win(0);
            } else {
                client2Win(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     
    public void waitingClientTimer() {
        waitingTimer = new CountDownTimer(12);
        waitingTimer.setTimerCallBack(
            null,
            (Callable) () -> {
                waitingTime = "" + CustumDateTimeFormatter.secondsToMinutes(waitingTimer.getCurrentTick());
                System.out.println("waiting: " + waitingTime);
                if (waitingTime.equals("00:00")) {
                    if (playAgainC1 == null && playAgainC2 == null) {
                        broadcast("ASK_PLAY_AGAIN;NO");
                        deleteRoom();
                    } 
                }
                return null;
            },
            1
        );
    }
    
    public void deleteRoom () {
        client1.setJoinedRoom(null);
        client1.setcCompetitor(null);
        client2.setJoinedRoom(null);
        client2.setcCompetitor(null);
        ServerRun.roomManager.remove(this);
    }
    
    public void resetRoom() {
        gameStarted = false;
        resultClient1 = null;
        resultClient2 = null;
        playAgainC1 = null;
        playAgainC2 = null;
        time = "00:00";
        waitingTime = "00:00";
    }
    
    private void getRandomProduct() {
        List<Product> products = new ArrayList<>();

        // Thêm các sản phẩm với tên và khoảng giá
        products.add(new Product("Điện thoại", 5000, 20000));
        products.add(new Product("Máy tính xách tay", 10000, 30000));
        products.add(new Product("Tai nghe", 200, 2000));
        products.add(new Product("Tivi", 5000, 15000));
        products.add(new Product("Máy ảnh", 3000, 15000));
        products.add(new Product("Chuột máy tính", 50, 500));
        products.add(new Product("Bàn phím", 100, 2000));
        products.add(new Product("USB", 50, 500));
        products.add(new Product("Thẻ nhớ", 30, 200));
        products.add(new Product("Ổ cứng di động", 500, 3000));
        products.add(new Product("Bút bi", 5, 20));
        products.add(new Product("Quạt mini", 30, 100));
        products.add(new Product("Đèn LED", 20, 100));
        products.add(new Product("Dây sạc", 10, 50));
        products.add(new Product("Sổ tay", 10, 50));
        products.add(new Product("Bình nước", 50, 200));
        products.add(new Product("Cốc giữ nhiệt", 50, 300));
        products.add(new Product("Tai nghe Bluetooth", 100, 1500));
        products.add(new Product("Loa Bluetooth", 100, 2000));
        products.add(new Product("Sạc dự phòng", 200, 1000));

        // Chọn ngẫu nhiên một sản phẩm và sinh giá ngẫu nhiên trong khoảng giá của nó
        Random random = new Random();
        Product randomProduct = products.get(random.nextInt(products.size()));
        
        int randomPrice = random.nextInt(randomProduct.maxPrice - randomProduct.minPrice + 1) + randomProduct.minPrice;
        
        currentProduct = randomProduct.name;
        currentPrice = randomPrice;
    }

    public void draw () throws SQLException {
        UserModel user1 = new UserController().getUser(client1.getLoginUser());
        UserModel user2 = new UserController().getUser(client2.getLoginUser());
        
        user1.setDraw(user1.getDraw() + 1);
        user2.setDraw(user2.getDraw() + 1);
        
        user1.setScore(user1.getScore()+ 0.5f);
        user2.setScore(user2.getScore()+ 0.5f);
        
//        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
//        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
//        
//        float newAvgCompetitor1 = (totalMatchUser1 * user1.getAvgCompetitor() + user2.getScore()) / (totalMatchUser1 + 1);
//        float newAvgCompetitor2 = (totalMatchUser2 * user1.getAvgCompetitor() + user1.getScore()) / (totalMatchUser2 + 1);
        
//        newAvgCompetitor1 = Math.round(newAvgCompetitor1 * 100) / 100;
//        newAvgCompetitor2 = Math.round(newAvgCompetitor2 * 100) / 100;
        
//        user1.setAvgCompetitor(newAvgCompetitor1);
//        user2.setAvgCompetitor(newAvgCompetitor2);
//        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
    }
    
    public void client1Win(int time) throws SQLException {
        UserModel user1 = new UserController().getUser(client1.getLoginUser());
        UserModel user2 = new UserController().getUser(client2.getLoginUser());
        
        user1.setWin(user1.getWin() + 1);
        user2.setLose(user2.getLose() + 1);
        
        user1.setScore(user1.getScore()+ 1);
        
//        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
//        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
//        
//        float newAvgCompetitor1 = (totalMatchUser1 * user1.getAvgCompetitor() + user2.getScore()) / (totalMatchUser1 + 1);
//        float newAvgCompetitor2 = (totalMatchUser2 * user1.getAvgCompetitor() + user1.getScore()) / (totalMatchUser2 + 1);
//        
//        user1.setAvgCompetitor(newAvgCompetitor1);
//        user2.setAvgCompetitor(newAvgCompetitor2);
//        
//        float newAvgTime1 = (totalMatchUser1 * user1.getAvgTime() + time) / (totalMatchUser1 + 1);
//        System.out.println("newAvgTime1: " + newAvgTime1);
//        user1.setAvgTime(newAvgTime1);
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
    }
    
    public void client2Win(int time) throws SQLException {
        UserModel user1 = new UserController().getUser(client1.getLoginUser());
        UserModel user2 = new UserController().getUser(client2.getLoginUser());
        
        user2.setWin(user2.getWin() + 1);
        user1.setLose(user1.getLose() + 1);
        
        user2.setScore(user2.getScore()+ 1);
        
//        int totalMatchUser1 = user1.getWin() + user1.getDraw() + user1.getLose();
//        int totalMatchUser2 = user2.getWin() + user2.getDraw() + user2.getLose();
//        
//        float newAvgCompetitor1 = (totalMatchUser1 * user1.getAvgCompetitor() + user2.getScore()) / (totalMatchUser1 + 1);
//        float newAvgCompetitor2 = (totalMatchUser2 * user1.getAvgCompetitor() + user1.getScore()) / (totalMatchUser2 + 1);
//        
//        user1.setAvgCompetitor(newAvgCompetitor1);
//        user2.setAvgCompetitor(newAvgCompetitor2);
//        
//        float newAvgTime2 = (totalMatchUser2 * user2.getAvgTime() + time) / (totalMatchUser2 + 1);
//        System.out.println("newAvgTime2: " + newAvgTime2);
//        user2.setAvgTime(newAvgTime2);
        
        new UserController().updateUser(user1);
        new UserController().updateUser(user2);
    }
    
    public void userLeaveGame (String username) throws SQLException {
        if (client1.getLoginUser().equals(username)) {
            client2Win(0);
        } else if (client2.getLoginUser().equals(username)) {
            client1Win(0);
        }
    }
    
    public String handlePlayAgain () {
        if (playAgainC1 == null || playAgainC2 == null) {
            return "NO";
        } else if (playAgainC1.equals("YES") && playAgainC2.equals("YES")) {
            return "YES";
        } else if (playAgainC1.equals("NO") && playAgainC2.equals("YES")) {
//            ServerRun.clientManager.sendToAClient(client2.getLoginUser(), "ASK_PLAY_AGAIN;NO");
//            deleteRoom();
            return "NO";
        } else if (playAgainC2.equals("NO") && playAgainC2.equals("YES")) {
//            ServerRun.clientManager.sendToAClient(client1.getLoginUser(), "ASK_PLAY_AGAIN;NO");
//            deleteRoom();
            return "NO";
        } else {
            return "NO";
        }
    }
    
    // add/remove client
    public boolean addClient(Client c) {
        if (!clients.contains(c)) {
            clients.add(c);
            if (client1 == null) {
                client1 = c;
            } else if (client2 == null) {
                client2 = c;
            }
            return true;
        }
        return false;
    }

    public boolean removeClient(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    // broadcast messages
    public void broadcast(String msg) {
        clients.forEach((c) -> {
            c.sendData(msg);
        });
    }
    
    public Client find(String username) {
        for (Client c : clients) {
            if (c.getLoginUser()!= null && c.getLoginUser().equals(username)) {
                return c;
            }
        }
        return null;
    }

    // gets sets
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient1() {
        return client1;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
    
    public int getSizeClient() {
        return clients.size();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResultClient1() {
        return resultClient1;
    }

    public void setResultClient1() {
        this.resultClient1 = "SUBMITTED";
    }

    public String getResultClient2() {
        return resultClient2;
    }

    public void setResultClient2() {
        this.resultClient2 = "SUBMITTED";
    }

    public String getPlayAgainC1() {
        return playAgainC1;
    }

    public void setPlayAgainC1(String playAgainC1) {
        this.playAgainC1 = playAgainC1;
    }

    public String getPlayAgainC2() {
        return playAgainC2;
    }

    public void setPlayAgainC2(String playAgainC2) {
        this.playAgainC2 = playAgainC2;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }
    
    
}
    
