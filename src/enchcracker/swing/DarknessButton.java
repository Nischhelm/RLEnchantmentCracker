package enchcracker.swing;

import java.awt.*;

public class DarknessButton extends ProgressButton {

    public DarknessButton(String img) {
        super(img);
    }

    @Override
    public void paint(Graphics g) {
        paintFirstHalf(g);
        if(!(isEnabled() || Float.isNaN(progress) || Float.isInfinite(progress))){
            g.setColor(new Color(102, 51, 210));
            g.fillRect(2, 2, Math.min(getWidth()-4, (int)((getWidth()-4)*progress)), getHeight()-4);
            g.setColor(Color.BLACK);
            String t = "Darkness " + Math.round(30-progress*30) +"s";
            drawButtonText(g, t);
        }
    }
}
