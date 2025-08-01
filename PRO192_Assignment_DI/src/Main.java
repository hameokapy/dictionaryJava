
public class Main {
    public static void main(String[] args){
        util.diutils.DIManager.initialize("out/production/PRO192_Assignment", "");
        view.ConsoleDictionaryView view = util.diutils.DIManager.getInstance(view.ConsoleDictionaryView.class);
        view.start();
    }
}
