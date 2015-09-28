package cci.myfridge.custom.classes;

/**
 * Created by Fabrice KISTNER on 6/25/2015.
 */
//
public class NavDrawerItemClass
{

    public int getNavDrawer_icon() {
        return navDrawer_icon;
    }

    public void setNavDrawer_icon(int navDrawer_icon) {
        this.navDrawer_icon = navDrawer_icon;
    }

    public int navDrawer_icon;
    public String navDrawer_title;

    public int getNavDrawer_count() {
        return navDrawer_count;
    }

    public void setNavDrawer_count(int navDrawer_count) {
        this.navDrawer_count = navDrawer_count;
    }

    public int navDrawer_count;

    public NavDrawerItemClass(int icon, String title, int count)
    {
        this.navDrawer_icon=icon;
        this.navDrawer_title=title;
        this.navDrawer_count=count;
    }


    public String getNavDrawer_title() {
        return navDrawer_title;
    }

    public void setNavDrawer_title(String navDrawer_title) {
        this.navDrawer_title = navDrawer_title;
    }

}
