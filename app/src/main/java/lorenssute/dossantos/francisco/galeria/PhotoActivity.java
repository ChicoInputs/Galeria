package lorenssute.dossantos.francisco.galeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar(); // obtém da Activity a ActionBar padrão
        actionBar.setDisplayHomeAsUpEnabled(true); // habilita o botão de voltar na ActionBar

        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path");//obtém o caminho da foto que foi envia via o Intent de criação

        Bitmap bitmap = Util.getBitmap(photoPath); //  carregue a foto em um Bitmap
        ImageView imPhoto = findViewById(R.id.imPhoto);// sete o Bitmap no ImageView
        imPhoto.setImageBitmap(bitmap);
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // O método acima será chamado sempre que um item da ToolBar for selecionado. Caso o ícone de câmera tenha sido clicado, então será executado código que compartilha a foto
        switch (item.getItemId()) {
            case R.id.opShare:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void sharePhoto() {
        // Codigo para compartilhar a foto
        Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this, "lorenssute.dossantos.francisco.galeria.filepreovider", new File(photoPath));
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, photoUri);
        i.setType("image/jpeg");
        startActivity(i);
    }
}