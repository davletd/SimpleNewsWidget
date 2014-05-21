package nsb.widget.news.utils.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

class ImageExtractor implements ImageGetter
{
    private String image;
    
    
    public ImageExtractor()
    {
    }
    
    @Override
    public Drawable getDrawable(String source)
    {
        //issue-10 google news hack
        if ( source != null && source.startsWith("//") ) source = "http:" + source;
        
        Bitmap bmp = ImageUtils.downloadImage(source);
        
        if ( bmp == null ) return null;
        
        if (this.image == null)
        {
            this.image = source;
        }
        
        return null;
    }
    
    public String getImage()
    {
        return image;
    }
}
