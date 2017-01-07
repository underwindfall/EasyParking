package fr.re21.easypark.customInterface;

/**
 * Created by maxime on 19/05/15.
 * Interface qui permet de gérer les réponses des requettes serveur
 */
public interface ServerResponseInterface {
    public void onEventCompleted(int method, String type);
    public void onEventFailed(int method, String type);
}