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
	private Stage _stage;
	private Scene _scene;
	private CheckBox _chbLower, _chbUpper, _chbNumbers, _chbSymbols;
	private Button _btnGenerate, _btnCopy;
	private TextField _txtLength, _txtPassword;
	private ImageView _imgClose;
	private Pane _paneActionsWrap;

	public HandlerManager(Stage stage)
	{
		_stage = stage;
		_scene = stage.getScene();

		_txtLength = (TextField) _scene.lookup("#txtLength");
		_chbLower = (CheckBox) _scene.lookup("#chbLower");
		_chbUpper = (CheckBox) _scene.lookup("#chbUpper");
		_chbNumbers = (CheckBox) _scene.lookup("#chbNumbers");
		_chbSymbols = (CheckBox) _scene.lookup("#chbSymbols");
		_btnGenerate = (Button) _scene.lookup("#btnGenerate");
		_btnCopy = (Button) _scene.lookup("#btnCopy");
		_txtPassword = (TextField) _scene.lookup("#txtPassword");
		_imgClose = (ImageView) _scene.lookup("#imgClose");
		_paneActionsWrap = (Pane) _scene.lookup("#actionsWrap");

		_initTxtLength();
		_initCheckBoxesValidation();
		_initBtnGenerate();
		_initBtnCopy();
		_initImgClose();
	}

	public void calculate()
	{
		// set disabled panel
		_paneActionsWrap.setDisable(true);

		Thread th = new Thread(() -> {
			String password = "";
			ArrayList<char[]> list = _getList();

			int n = Integer.valueOf(_txtLength.getText());
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

			final String _p = password;

			Platform.runLater(() -> {
				_paneActionsWrap.setDisable(false);
				_txtPassword.setText(_p);
				_btnGenerate.requestFocus();
			});
		});

		th.start();
	}

	private void _initBtnGenerate()
	{
		_btnGenerate.requestFocus();

		_btnGenerate.setOnAction(event -> calculate());
	}

	private ArrayList<char[]> _getList()
	{
		ArrayList<char[]> list = new ArrayList<>();

		if (_chbLower.isSelected()) {
			char[] nl = new char[26];

			for (int i = 0; i < 26; i++)
				nl[i] = (char) (97 + i);

			list.add(nl);
		}

		if (_chbUpper.isSelected()) {
			char[] nl = new char[26];

			for (int i = 0; i < 26; i++)
				nl[i] = (char) (65 + i);

			list.add(nl);
		}

		if (_chbNumbers.isSelected()) {
			char[] nl = new char[10];

			for (int i = 0; i < 10; i++)
				nl[i] = String.valueOf(i).charAt(0);

			list.add(nl);
		}

		if (_chbSymbols.isSelected())
			list.add("!@#$%^&*()><{}-_+~[]".toCharArray());

		Collections.shuffle(list);

		return list;
	}

	private void _initBtnCopy()
	{
		_btnCopy.setOnAction(event -> {
			Clipboard c = Clipboard.getSystemClipboard();
			ClipboardContent cb = new ClipboardContent();
			cb.putString(_txtPassword.getText());
			c.setContent(cb);
		});
	}

	private void _initImgClose()
	{
		_imgClose.setCursor(Cursor.HAND);
		final Image normal = new Image("/images/close.png");
		final Image hover = new Image("/images/closeh.png");

		_imgClose.setOnMouseClicked(event -> _stage.close());

		_imgClose.setOnMouseEntered(event -> _imgClose.setImage(hover));

		_imgClose.setOnMouseExited(event -> _imgClose.setImage(normal));
	}

	private void _initTxtLength()
	{
		_txtLength.textProperty().addListener((observable, oldValue, newValue) -> {
			Pattern p = Pattern.compile("^[0-9]*$");
			Matcher m = p.matcher(newValue);

			if (!m.find())
				_txtLength.setText(oldValue);
			else if (newValue.length() == 0)
				_txtLength.setText("1");
		});

		_txtLength.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.UP))
				_txtLength.setText(String.valueOf(Integer.valueOf(_txtLength.getText()) + 1));
			else if (event.getCode().equals(KeyCode.DOWN))
				_txtLength.setText(String.valueOf(Integer.valueOf(_txtLength.getText()) - (Integer.valueOf(_txtLength.getText()) > 1 ? 1 : 0)));
		});
	}

	private void _initCheckBoxesValidation()
	{
		Set<Node> nodes = _scene.lookup("#wrap").lookupAll("CheckBox.chbs");

		for (Node node : nodes) {
			final CheckBox cb = (CheckBox) node;

			cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (_isLast(cb) && !newValue)
					cb.setSelected(true);
			});
		}
	}

	private boolean _isLast(CheckBox noChb)
	{
		int n = 0;

		Set<Node> nodes = _scene.lookup("#wrap").lookupAll("CheckBox.chbs");

		for (Node node : nodes) {
			if (noChb.getId().equals(node.getId()))
				continue;

			n += ((CheckBox) node).isSelected() ? 1 : 0;
		}

		return n == 0;
	}
}
