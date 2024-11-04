package run;

import controller.SocketHandler;
import javax.swing.JOptionPane;
import view.GameView;
import view.HomeView;
import view.InfoPlayerView;
import view.LoginView;
import view.MessageView;
import view.RegisterView;
import view.LeaderboardView;
public class ClientRun {
    public enum SceneName {
        LOGIN,
        REGISTER,
        HOMEVIEW,
        INFOPLAYER,
        MESSAGEVIEW,
        GAMEVIEW,
        LEADERBOARDVIEW
    }

    // scenes
    public static LoginView loginView;
    public static RegisterView registerView;
    public static HomeView homeView;
    public static GameView gameView;
    public static InfoPlayerView infoPlayerView;                        
    public static MessageView messageView;
    public static LeaderboardView leaderboardView;

    // controller 
    // change
    // public static SocketHandler socketHandler;
    private SocketHandler socketHandler;

    public ClientRun() {
        socketHandler = new SocketHandler();
        initScene();
        connect();
    }
    
    public void initScene() {
        loginView = new LoginView();
        registerView = new RegisterView();
        homeView = new HomeView();
        infoPlayerView = new InfoPlayerView();
        messageView = new MessageView();
        gameView = new GameView();
        leaderboardView = new LeaderboardView();
    }
    
    private void connect() {
        String ip = "127.0.0.1";
        int port = 8282;
        // connect to server
        new Thread(() -> {
            // call controller
            String result = ClientRun.getSocketHandler().connect(ip, port);

            // check result
            if (result.equals("success")) {
                onSuccess();
            } else {
                String failedMsg = result.split(";")[1];
                onFailed(failedMsg);
            }
        }).start();
    }
    
    private void onSuccess() {
        openScene(SceneName.LOGIN);

        System.out.println("connect to server thanh cong");
    }

    private void onFailed(String failedMsg) {
        JOptionPane.showMessageDialog(null, failedMsg, "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void openScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case LOGIN:
                    loginView = new LoginView();
                    loginView.setVisible(true);
                    break;
                case REGISTER:
                    registerView = new RegisterView();
                    registerView.setVisible(true);
                    break;
                case HOMEVIEW:
                    homeView = new HomeView();
                    homeView.setVisible(true);
                    break;
                case INFOPLAYER:
                    infoPlayerView = new InfoPlayerView();
                    infoPlayerView.setVisible(true);
                    break;
                case MESSAGEVIEW:
                    messageView = new MessageView();
                    messageView.setVisible(true);
                    break;
                case GAMEVIEW:
                    gameView = new GameView();
                    gameView.setVisible(true);
                    break;
                case LEADERBOARDVIEW:
                    leaderboardView = new LeaderboardView();
                    leaderboardView.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case LOGIN:
                    loginView.dispose();
                    break;
                case REGISTER:
                    registerView.dispose();
                    break;
                case HOMEVIEW:
                    homeView.dispose();
                    break;
                case INFOPLAYER:
                    infoPlayerView.dispose();
                    break;
                case MESSAGEVIEW:
                    messageView.dispose();
                    break;
                case GAMEVIEW:
                    gameView.dispose();
                    break;
                case LEADERBOARDVIEW:
                    leaderboardView.dispose();
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeAllScene() {
        loginView.dispose();
        registerView.dispose();
        homeView.dispose();
        infoPlayerView.dispose();
        messageView.dispose();
        gameView.dispose();
        leaderboardView.dispose();
    }

    public static SocketHandler getSocketHandler() {
        return getInstance().socketHandler;
    }

    private static ClientRun instance;

    public static ClientRun getInstance() {
        if (instance == null) {
            instance = new ClientRun();
        }
        return instance;
    }

    public static void main(String[] args) {
        getInstance();
    }
}
