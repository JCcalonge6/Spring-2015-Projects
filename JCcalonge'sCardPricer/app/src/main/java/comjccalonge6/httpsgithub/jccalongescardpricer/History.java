package comjccalonge6.httpsgithub.jccalongescardpricer;

import android.net.Uri;

/**
 * Created by JCcalonge on 5/11/2015.
 */
public class History {

    private String _cardName;
    private Uri _imageURI;
    private int _id;

    public History(int id, String cardName, Uri imageURI){
        _id = id;
        _cardName = cardName;
        _imageURI = imageURI;
    }

    public int getId() {
        return _id;
    }

    public String getCardName() {
        return _cardName;
    }

    public Uri getImageURI() {
        return _imageURI;
    }
}
