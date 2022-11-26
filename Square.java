import javafx.scene.control.Button;

public class Square extends Button{
    protected int x;
    protected int y;
    private int border;
    protected boolean marked;
    protected boolean shown;

    public Square(int inx, int iny) {
        super(" ");
        x = inx;
        y = iny;
        border = 0;
        marked = false;
        shown = false;
        setMinSize(30.0, 30.0);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean getShown(){
        return shown;
    }

    public int getBorderNum() {
        return border;
    }

    public void show(){
        if(!marked) {
            if (border != 0) {
                setText(Integer.toString(border));
            }
            shown = true;
            setStyle("-fx-background-color: #f3f3f3; ");
        }
    }

    public void mark(){
        if(!shown) {
            if (!marked) {
                setText(new String(Character.toChars(0x1F6A9)));
                marked = true;
            } else {
                setText(" ");
                marked = false;
            }
        }
    }

    public boolean getMarked(){
        return marked;
    }

    public void increment(){
        border++;
    }

}
