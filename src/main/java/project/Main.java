package project;

public class Main {
    public static void main(String[] args) {
        Backend backend = new Backend();
        Menu menu = new Menu(backend);
        menu.display();
    }
}
