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

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerManager
{
	private Stage stage;
	private Scene scene;
	private CheckBox chbLower, chbUpper, chbNumbers, chbSymbols;
	private Button btnGenerate, btnCopy;
	private TextField txtLength, txtPassword;
	private ImageView imgClose;
	private Pane paneActionsWrap;

	public HandlerManager(Stage stage)
	{
		this.stage = stage;
		scene = stage.getScene();

		txtLength = (TextField) scene.lookup("#txtLength");
		chbLower = (CheckBox) scene.lookup("#chbLower");
		chbUpper = (CheckBox) scene.lookup("#chbUpper");
		chbNumbers = (CheckBox) scene.lookup("#chbNumbers");
		chbSymbols = (CheckBox) scene.lookup("#chbSymbols");
		btnGenerate = (Button) scene.lookup("#btnGenerate");
		btnCopy = (Button) scene.lookup("#btnCopy");
		txtPassword = (TextField) scene.lookup("#txtPassword");
		imgClose = (ImageView) scene.lookup("#imgClose");
		paneActionsWrap = (Pane) scene.lookup("#actionsWrap");

		initTxtLength();
		initCheckBoxesValidation();
		initBtnGenerate();
		initBtnCopy();
		initImgClose();
	}

	public void calculate()
	{
		// set disabled panel
		paneActionsWrap.setDisable(true);

		Thread th = new Thread(() -> {
			String password = "";
			ArrayList<char[]> list = getList();

			int n = Integer.valueOf(txtLength.getText());
			int nlist = list.size();
			int i = 0;

			Random random = new Random();

			while (password.length() != n) {
				password += list.get(i)[random.nextInt(list.get(i).length)];
				i = ++i == nlist ? 0 : i;
			}

			List<String> str = Arrays.asList(password.split(""));
			Collections.shuffle(str);

			password = "";

			for (String aStr : str) password += aStr;

			final String p = password;

			Platform.runLater(() -> {
				paneActionsWrap.setDisable(false);
				txtPassword.setText(p);
				btnGenerate.requestFocus();
			});
		});

		th.start();
	}

	private void initBtnGenerate()
	{
		btnGenerate.requestFocus();

		btnGenerate.setOnAction(event -> calculate());
	}

	private ArrayList<char[]> getList()
	{
		ArrayList<char[]> list = new ArrayList<>();

		if (chbLower.isSelected()) {
			char[] nl = new char[26];

			for (int i = 0; i < 26; i++)
				nl[i] = (char) (97 + i);

			list.add(nl);
		}

		if (chbUpper.isSelected()) {
			char[] nl = new char[26];

			for (int i = 0; i < 26; i++)
				nl[i] = (char) (65 + i);

			list.add(nl);
		}

		if (chbNumbers.isSelected()) {
			char[] nl = new char[10];

			for (int i = 0; i < 10; i++)
				nl[i] = String.valueOf(i).charAt(0);

			list.add(nl);
		}

		if (chbSymbols.isSelected())
			list.add("!@#$%^&*()><{}-_+~[]".toCharArray());

		Collections.shuffle(list);

		return list;
	}

	private void initBtnCopy()
	{
		btnCopy.setOnAction(event -> {
			Clipboard c = Clipboard.getSystemClipboard();
			ClipboardContent cb = new ClipboardContent();
			cb.putString(txtPassword.getText());
			c.setContent(cb);
		});
	}

	private void initImgClose()
	{
		imgClose.setCursor(Cursor.HAND);
		final Image normal = new Image("/images/close.png");
		final Image hover = new Image("/images/closeh.png");

		imgClose.setOnMouseClicked(event -> stage.close());

		imgClose.setOnMouseEntered(event -> imgClose.setImage(hover));

		imgClose.setOnMouseExited(event -> imgClose.setImage(normal));
	}

	private void initTxtLength()
	{
		txtLength.textProperty().addListener((observable, oldValue, newValue) -> {
			final Pattern p = Pattern.compile("^[0-9]*$");
			final Matcher m = p.matcher(newValue);

			if (!m.find())
				txtLength.setText(oldValue);
			else if (newValue.length() == 0)
				txtLength.setText("1");
		});

		txtLength.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.UP))
				txtLength.setText(String.valueOf(Integer.valueOf(txtLength.getText()) + 1));
			else if (event.getCode().equals(KeyCode.DOWN))
				txtLength.setText(String.valueOf(Integer.valueOf(txtLength.getText()) - (Integer.valueOf(txtLength.getText()) > 1 ? 1 : 0)));
		});
	}

	private void initCheckBoxesValidation()
	{
		Set<Node> nodes = scene.lookup("#wrap").lookupAll("CheckBox.chbs");

		for (Node node : nodes) {
			final CheckBox cb = (CheckBox) node;

			cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (isLast(cb) && !newValue)
					cb.setSelected(true);
			});
		}
	}

	private boolean isLast(CheckBox noChb)
	{
		int n = 0;

		Set<Node> nodes = scene.lookup("#wrap").lookupAll("CheckBox.chbs");

		for (Node node : nodes) {
			if (noChb.getId().equals(node.getId()))
				continue;

			n += ((CheckBox) node).isSelected() ? 1 : 0;
		}

		return n == 0;
	}
}
