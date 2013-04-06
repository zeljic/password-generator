/*
 * Copyright 2013 Djordje Zeljic <zeljic@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zeljic.pwdgen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.zeljic.pwdgen.gfx.StageMoveHandler;

public class Boot extends Application
{
	private Stage _stage;
	private Scene _scene;

	@Override
	public void start(Stage stage) throws Exception
	{
		_stage = stage;

		stage.initStyle(StageStyle.TRANSPARENT);
		_scene = new Scene((Parent) FXMLLoader.load(getClass().getResource("/fxml/Boot.fxml")));
		stage.setScene(_scene);

		stage.setTitle("Password Generator");
		stage.setResizable(false);
		_scene.setFill(Color.TRANSPARENT);

		stage.show();

		new StageMoveHandler().init(_stage, _scene.lookup("#wrap"));
		new HandlerManager(_stage).calculate();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
