/*
 * Copyright 2013 Đorđe Zeljić <zeljic@gmail.com>.
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
package com.zeljic.pwdgen.gfx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class StageMoveHandler
{
	private double x, y;

	public void init(final Stage stage, Node panel)
	{
		panel.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					stage.setOpacity(.6);

					x = stage.getX() - event.getScreenX();
					y = stage.getY() - event.getScreenY();
				}
			}
		});

		panel.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event)
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					stage.setX(event.getScreenX() + x);
					stage.setY(event.getScreenY() + y);
				}
			}
		});

		panel.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event)
			{
				stage.setOpacity(1);
			}
		});
	}
}
