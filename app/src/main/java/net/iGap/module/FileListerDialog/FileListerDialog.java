package net.iGap.module.FileListerDialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;

import java.io.File;

/**
 * A File Lister Dialog
 */

public class FileListerDialog {

    /**
     * File Filter for the FileListerDialog
     */
    public enum FILE_FILTER {
        /**
         * List All Files
         */
        ALL_FILES,
        /**
         * List only directories
         */
        DIRECTORY_ONLY,
        /**
         * List Directory and Image files
         */
        IMAGE_ONLY,
        /**
         * List Directory and Video files
         */
        VIDEO_ONLY,
        /**
         * List Directory and Audio files
         */
        AUDIO_ONLY
    }

    private MaterialDialog.Builder mDialog;

    private FilesListerView filesListerView;

    private OnFileSelectedListener onFileSelectedListener;

    private FileListerDialog(@NonNull Context context) {
        //super(context);
        mDialog = new MaterialDialog.Builder(context);
        init(context);
    }

    private FileListerDialog(@NonNull Context context, int themeResId) {
        //super(context, themeResId);
        mDialog = new MaterialDialog.Builder(context);
        init(context);
    }

    /**
     * Creates a default instance of FileListerDialog
     *
     * @param context Context of the App
     * @return Instance of FileListerDialog
     */
    public static FileListerDialog createFileListerDialog(@NonNull Context context) {
        return new FileListerDialog(context);
    }

    /**
     * Creates an instance of FileListerDialog with the specified Theme
     *
     * @param context Context of the App
     * @param themeId Theme Id for the dialog
     * @return Instance of FileListerDialog
     */
    public static FileListerDialog createFileListerDialog(@NonNull Context context, int themeId) {
        return new FileListerDialog(context, themeId);
    }

    private void init(Context context) {
        filesListerView = new FilesListerView(context);
        mDialog.customView(filesListerView, false);
        mDialog.positiveText(R.string.Select);
        mDialog.neutralText(R.string.Default_Dir);
        mDialog.negativeText(R.string.cancel);
        mDialog.onPositive((dialog, which) -> {
            dialog.dismiss();
            if (onFileSelectedListener != null)
                onFileSelectedListener.onFileSelected(filesListerView.getSelected(), filesListerView.getSelected().getAbsolutePath());
        });

        mDialog.onNeutral((dialog, which) -> filesListerView.goToDefaultDir());
    }

    /**
     * Display the FileListerDialog
     */
    public void show() {
        //getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        switch (filesListerView.getFileFilter()) {
            case DIRECTORY_ONLY:
                mDialog.title(R.string.Select_directory);
                break;
            case VIDEO_ONLY:
                mDialog.title(R.string.Select_Video_file);
                break;
            case IMAGE_ONLY:
                mDialog.title(R.string.Select_Image_file);
                break;
            case AUDIO_ONLY:
                mDialog.title(R.string.Select_Audio_file);
                break;
            case ALL_FILES:
                mDialog.title(R.string.Select_file);
                break;
            default:
                mDialog.title(R.string.Select_file);
        }
        filesListerView.start();
        mDialog.show();
    }

    /**
     * Listener to know which file/directory is selected
     *
     * @param onFileSelectedListener Instance of the Listener
     */
    public void setOnFileSelectedListener(OnFileSelectedListener onFileSelectedListener) {
        this.onFileSelectedListener = onFileSelectedListener;
    }

    /**
     * Set the initial directory to show the list of files in that directory
     *
     * @param file File pointing to the directory
     */
    public void setDefaultDir(@NonNull File file) {
        filesListerView.setDefaultDir(file);
    }

    /**
     * Set the initial directory to show the list of files in that directory
     *
     * @param file String denoting to the directory
     */
    public void setDefaultDir(@NonNull String file) {
        filesListerView.setDefaultDir(file);
    }

    /**
     * Set the file filter for listing the files
     *
     * @param fileFilter One of the FILE_FILTER values
     */
    public void setFileFilter(FILE_FILTER fileFilter) {
        filesListerView.setFileFilter(fileFilter);
    }

}
