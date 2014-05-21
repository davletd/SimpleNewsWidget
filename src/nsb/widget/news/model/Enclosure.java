package nsb.widget.news.model;

import java.io.Serializable;
import java.net.URL;

@SuppressWarnings("serial") //$NON-NLS-1$
public class Enclosure implements Serializable
{
    
    private long mId = -1;
    private String mMime;
    private URL mURL;
    
    public Enclosure()
    {
    }
    
    public Enclosure(long id, String mime, URL url)
    {
        this.mId = id;
        this.mMime = mime;
        this.mURL = url;
    }
    
    public void setId(long id)
    {
        this.mId = id;
    }
    
    public long getId()
    {
        return mId;
    }
    
    public void setMime(String mime)
    {
        this.mMime = mime;
    }
    
    public String getMime()
    {
        return this.mMime;
    }
    
    public void setURL(URL url)
    {
        this.mURL = url;
    }
    
    public URL getURL()
    {
        return this.mURL;
    }
    
    public String toString()
    {
        return "{" + //$NON-NLS-1$
		     "id: " + this.mId +  //$NON-NLS-1$
		     ", mime: " + this.mMime +  //$NON-NLS-1$
		     ", url: " + this.mURL.toString() + //$NON-NLS-1$
		     "}"; //$NON-NLS-1$
    }
}
