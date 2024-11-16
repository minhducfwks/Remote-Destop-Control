package TestRunner;

import java.awt.dnd.DropTarget;

import javax.swing.JFrame;

public class TestDrop extends JFrame{
    public TestDrop(){
        this.setTitle("demo");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(3);
        this.setDropTarget(new DropTarget(this,new Drop()));
        this.setVisible(true);
    }
    public static void main(String[] args) {
        new TestDrop();
    }
}
