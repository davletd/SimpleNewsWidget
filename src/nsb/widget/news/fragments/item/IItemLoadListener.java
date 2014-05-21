package nsb.widget.news.fragments.item;

import nsb.widget.news.model.Item;

public interface IItemLoadListener
{
    void itemLoaded(Item item);
    void startLoading();
}
