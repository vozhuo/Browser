package cf.vozhuo.app.broswser.favorites;

import cf.vozhuo.app.broswser.tab.Tab;

public interface FavoritesController {
    void modify(FavoritesEntity favorites);
    void delete(FavoritesEntity favorites);
}
