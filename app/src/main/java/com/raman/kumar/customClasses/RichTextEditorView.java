package com.raman.kumar.customClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.raman.kumar.shrikrishan.R;

import jp.wasabeef.richeditor.RichEditor;

public class RichTextEditorView extends LinearLayout {

    private RichEditor richEditor;

    // Flags to track button selection states
    private boolean isBoldActive = false;
    private boolean isItalicActive = false;
    private boolean isUnderlineActive = false;
    private boolean isBulletsActive = false;
    private boolean isNumbersActive = false;

    // Flags to track alignment states
    private boolean isAlignLeftActive = false;
    private boolean isAlignCenterActive = false;
    private boolean isAlignRightActive = false;
    private boolean isAlignJustifyActive = false;

    public RichTextEditorView(Context context) {
        super(context);
        init(context);
    }

    public RichTextEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.activity_rich_text_editor_view, this, true);
        LayoutInflater.from(context).inflate(R.layout.activity_rich_text_editor_view, this, true);

        richEditor = findViewById(R.id.richEditor);
        if (richEditor == null) {
            Log.e("RichTextEditorView", "RichEditor is null. Check your layout.");
        } else {
            Log.d("RichTextEditorView", "RichEditor initialized successfully.");
        }


        // Initialize the RichEditor
        richEditor = findViewById(R.id.richEditor);
        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(18);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("Write something...");

        // Handle clicks on the ImageViews instead of Buttons
        findViewById(R.id.btnBold).setOnClickListener(v -> toggleBold());
        findViewById(R.id.btnItalic).setOnClickListener(v -> toggleItalic());
        findViewById(R.id.btnUnderline).setOnClickListener(v -> toggleUnderline());
        findViewById(R.id.btnBullet).setOnClickListener(v -> toggleBullets());
        findViewById(R.id.btnNumbered).setOnClickListener(v -> toggleNumbers());

        // Alignment actions
        findViewById(R.id.btnAlignLeft).setOnClickListener(v -> toggleAlignLeft());
        findViewById(R.id.btnAlignCenter).setOnClickListener(v -> toggleAlignCenter());
        findViewById(R.id.btnAlignRight).setOnClickListener(v -> toggleAlignRight());
        findViewById(R.id.btnAlignJustify).setOnClickListener(v -> toggleAlignJustify());

        // Insert link
        findViewById(R.id.btnInsertLink).setOnClickListener(v -> richEditor.insertLink("https://example.com", "Example Link"));

        // Undo/Redo
        findViewById(R.id.btnUndo).setOnClickListener(v -> richEditor.undo());
        findViewById(R.id.btnRedo).setOnClickListener(v -> richEditor.redo());
    }

    // Formatting actions (Bold, Italic, Underline, Bullets, Numbers)
    private void toggleBold() {
        if (isBoldActive) {
            richEditor.setBold(); // Deselect
            isBoldActive = false;
            updateImageAppearance(R.id.btnBold, false);
        } else {
            richEditor.setBold(); // Select
            isBoldActive = true;
            updateImageAppearance(R.id.btnBold, true);
        }
    }

    private void toggleItalic() {
        if (isItalicActive) {
            richEditor.setItalic(); // Deselect
            isItalicActive = false;
            updateImageAppearance(R.id.btnItalic, false);
        } else {
            richEditor.setItalic(); // Select
            isItalicActive = true;
            updateImageAppearance(R.id.btnItalic, true);
        }
    }

    private void toggleUnderline() {
        if (isUnderlineActive) {
            richEditor.setUnderline(); // Deselect
            isUnderlineActive = false;
            updateImageAppearance(R.id.btnUnderline, false);
        } else {
            richEditor.setUnderline(); // Select
            isUnderlineActive = true;
            updateImageAppearance(R.id.btnUnderline, true);
        }
    }

    private void toggleBullets() {
        if (isBulletsActive) {
            richEditor.setBullets(); // Deselect
            isBulletsActive = false;
            updateImageAppearance(R.id.btnBullet, false);
        } else {
            richEditor.setBullets(); // Select
            isBulletsActive = true;
            updateImageAppearance(R.id.btnBullet, true);
        }
    }

    private void toggleNumbers() {
        if (isNumbersActive) {
            richEditor.setNumbers(); // Deselect
            isNumbersActive = false;
            updateImageAppearance(R.id.btnNumbered, false);
        } else {
            richEditor.setNumbers(); // Select
            isNumbersActive = true;
            updateImageAppearance(R.id.btnNumbered, true);
        }
    }

    // Alignment toggle methods
    private void toggleAlignLeft() {
        if (isAlignLeftActive) {
            richEditor.setAlignLeft();  // Deselect left alignment
            isAlignLeftActive = false;
            updateImageAppearance(R.id.btnAlignLeft, false);
        } else {
            richEditor.setAlignLeft();  // Select left alignment
            isAlignLeftActive = true;
            updateImageAppearance(R.id.btnAlignLeft, true);

            // Deselect other alignments
            deselectOtherAlignments(R.id.btnAlignLeft);
        }
    }

    private void toggleAlignCenter() {
        if (isAlignCenterActive) {
            richEditor.setAlignCenter(); // Deselect center alignment
            isAlignCenterActive = false;
            updateImageAppearance(R.id.btnAlignCenter, false);
        } else {
            richEditor.setAlignCenter(); // Select center alignment
            isAlignCenterActive = true;
            updateImageAppearance(R.id.btnAlignCenter, true);

            // Deselect other alignments
            deselectOtherAlignments(R.id.btnAlignCenter);
        }
    }

    private void toggleAlignRight() {
        if (isAlignRightActive) {
            richEditor.setAlignRight(); // Deselect right alignment
            isAlignRightActive = false;
            updateImageAppearance(R.id.btnAlignRight, false);
        } else {
            richEditor.setAlignRight(); // Select right alignment
            isAlignRightActive = true;
            updateImageAppearance(R.id.btnAlignRight, true);

            // Deselect other alignments
            deselectOtherAlignments(R.id.btnAlignRight);
        }
    }

    private void toggleAlignJustify() {
        if (isAlignJustifyActive) {
            // Justify alignment has been deselected
            richEditor.setHtml("<div style=\"text-align:left\">" + richEditor.getHtml() + "</div>");
            isAlignJustifyActive = false;
            updateImageAppearance(R.id.btnAlignJustify, false);
        } else {
            // Justify alignment has been selected
            richEditor.setHtml("<div style=\"text-align:justify\">" + richEditor.getHtml() + "</div>");
            isAlignJustifyActive = true;
            updateImageAppearance(R.id.btnAlignJustify, true);
        }

        // Deselect other alignments
        deselectOtherAlignments(R.id.btnAlignJustify);
    }


    // Helper method to update the appearance of the icons
    private void updateImageAppearance(int imageViewId, boolean isSelected) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setColorFilter(isSelected ? Color.GREEN : Color.WHITE); // Update the color filter as an indicator
    }

    // Helper method to deselect other alignment buttons
    private void deselectOtherAlignments(int selectedButtonId) {
        int[] alignmentButtons = {R.id.btnAlignLeft, R.id.btnAlignCenter, R.id.btnAlignRight, R.id.btnAlignJustify};
        for (int buttonId : alignmentButtons) {
            if (buttonId != selectedButtonId) {
                updateImageAppearance(buttonId, false);
            }
        }
    }
    public void setHtmlContent(String html) {
        richEditor.setHtml(html);
    }
    public String getHtmlContent() {
        return richEditor.getHtml();
    }
}

