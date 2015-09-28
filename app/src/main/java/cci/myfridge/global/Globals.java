package cci.myfridge.global;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cci.myfridge.R;
import cci.myfridge.custom.classes.ProductsClass;


public class Globals extends Application {

    Intent intent;
    public ArrayList<ProductsClass> mProductsList=new ArrayList<>();
    public ArrayList<ProductsClass> mNotiList=new ArrayList<>();
    public boolean editProfile=false;
    public boolean editProduct=false;
    public boolean allowNoti=false;
    public String threshhold="";
    public boolean callFromNotification=false;
    public boolean callFromLoginActivity=false;
    public boolean callFromRegisterActivity=false;

    // Ouvre Activity
	public void OpenActivity(Context context, Activity activity, Class<?> cls, int animIn, int animOut) {
		intent = new Intent(context, cls);
		context.startActivity(intent);
		activity.overridePendingTransition(animIn, animOut);
	}
    // Encode l'image en base 64
    public String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }
    // Créer l'image en forme de cercle
    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        //bitmap.recycle();

        return output;
    }
        // Remplace les fragments
    public void ReplaceFragment(Fragment fragment, FragmentManager manager) {
        FragmentManager fragmentManager = manager;

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack(null).commit();

    }
    // Decode base 64 en bitmap
    public Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    // Valide l'email
    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
