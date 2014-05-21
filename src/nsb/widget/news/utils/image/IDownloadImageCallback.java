package nsb.widget.news.utils.image;

import java.io.File;

public interface IDownloadImageCallback
{

    void downloadCompleted(File f);

    void downloadFailed();
    
}
