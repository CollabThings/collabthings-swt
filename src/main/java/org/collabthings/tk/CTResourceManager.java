package org.collabthings.tk;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

public interface CTResourceManager {

	Color getControlBg();

	Font getDefaultFont();

	Font getTitleFont();

	Color getTitleColor();

	Font getDefaultFont(int size, int style);

	Color getActiontitle2Background();

	Color getActionTitle2Color();

	Color getActiontitleBackground();

	Color getColor(int colorWidgetHighlightShadow);

	Color getColor(int i, int j, int k);

	Font getFont(String string, int i, int normal);

	Color getColor(RGB rgb);

	RGB getRGBWithDoubled(double red, double green, double blue);

	Color getTabNotSelectedColor();

	Color getTabSelectedColor();

	Color getTextEditorColor();

	Color getTextErrorColor();

}
