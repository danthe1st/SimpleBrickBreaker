package io.github.danthe1st.brickbreaker.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.danthe1st.brickbreaker.BrickBreaker;

public class DesktopLauncher {
	public static void main (String[] arg) {
		boolean debug=System.getProperty("ebug")!=null;
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height*=2;
		config.width=config.height*4/3;
		config.title="SimpleBrickBreaker";
		config.undecorated=!debug;
		config.resizable=false;
		config.fullscreen=!debug;//-Debug
		new LwjglApplication(new BrickBreaker(), config);
	}
}
