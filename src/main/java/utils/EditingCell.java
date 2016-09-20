package utils;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.AcceptOnExitTableCell;
import tableviews.ProductKindPropertiesTableView;

/**
 * Created by developer on 06.09.16.
 */
public class EditingCell extends AcceptOnExitTableCell{

    private TextField textField;
    private String lastKey = null;

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            Platform.runLater(() -> {//without this space erases text, f2 doesn't
                textField.requestFocus();//also selects
            });
            if (lastKey != null) {
                textField.setText(lastKey);
                Platform.runLater(() -> {
                    textField.deselect();
                    textField.end();
                    textField.positionCaret(textField.getLength()+2);
                    //textField.positionCaret(textField.getLength()+2);//works sometimes
                });
            }
        }
    }

    public void commit(){
        super.commitEdit(textField.getText());
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        try {
            setText(getItem().toString());
        } catch (Exception e) {}
        setGraphic(null);
    }

    @Override
    public void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            if (textField != null) {
                textField.setText(getString());
            }
            setText(null);
            setGraphic(textField);
        } else {
            setText(getString());
            setGraphic(null);
            if (getTableColumn().getText().equals("amount"))
                setAlignment(Pos.CENTER_RIGHT);
        }
    }

    private void createTextField() {
        textField = new TextField(getString());

        //doesn't work if clicking a different cell, only focusing out of table
        textField.focusedProperty().addListener(
                (ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                    if (!arg2) commitEdit(textField.getText());
                });

        textField.setOnKeyReleased((KeyEvent t) -> {
            if ((t.getCode() == KeyCode.TAB) || (t.getCode() == KeyCode.ENTER)) {
                commitEdit(textField.getText());
                EditingCell.this.getTableView().requestFocus();//why does it lose focus??
                EditingCell.this.getTableView().getSelectionModel().selectBelowCell();
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        textField.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {
            if (t.getCode() == KeyCode.DELETE) {
                t.consume();//stop from deleting line in table keyevent
            }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
