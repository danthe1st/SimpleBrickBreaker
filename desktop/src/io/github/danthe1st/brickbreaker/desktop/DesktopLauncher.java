package io.github.danthe1st.brickbreaker.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.danthe1st.brickbreaker.BrickBreaker;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height= (int) (config.width*0.75);
		config.resizable=false;
		new LwjglApplication(new BrickBreaker(), config);
	}
}
