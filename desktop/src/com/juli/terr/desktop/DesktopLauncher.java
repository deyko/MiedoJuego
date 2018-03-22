package com.juli.terr.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.juli.terr.MyTestGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = MyTestGame.WIDTH;
		config.height = MyTestGame.HEIGHT;
		config.title = MyTestGame.TITLE;
		new LwjglApplication(new MyTestGame(), config);
	}
}









