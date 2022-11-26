public class Mine extends Square {
    public Mine(int inx, int iny){
        super(inx, iny);
        setMinSize(30.0, 30.0);
    }
    public void show(){
        if(!marked) {
            setText(new String(Character.toChars(0x1F4A3)));
            shown = true;
            setStyle("-fx-background-color: #f3f3f3; ");
        }
    }
}
