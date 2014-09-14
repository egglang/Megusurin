package ma10.megusurin;



import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Camera Preview Fragment
 */
public class CameraViewFragment extends Fragment {

    public CameraViewFragment() {
    }

    private CameraPreview mPreview;

    private Camera mCamera;

    private int mDefaultCameraId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the Camera View
        mPreview = new CameraPreview(getActivity());

        // Find the total number of cameras available
        int numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mDefaultCameraId = i;
            }
        }

        return mPreview;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCamera = Camera.open(mDefaultCameraId);
        mPreview.setCamera(mCamera);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }
}

