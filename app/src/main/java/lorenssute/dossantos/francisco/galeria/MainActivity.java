package lorenssute.dossantos.francisco.galeria;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> photos = new ArrayList<>();
    MainAdapter mainAdapter;
    static int RESULT_TAKE_PICTURE = 1;

    String currentPhotoPath;

    static int RESULT_REQUEST_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        checkForPermissions(permissions);

        Toolbar toolbar = findViewById(R.id.tbMain); // obtem o elemento tbMain
        setSupportActionBar(toolbar); //  indica para MainActivity que tbMain deve ser considerado como a ActionBar padrão da tela

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = dir.listFiles(); //  leem a lista de fotos já salvas
        for (int i = 0; i < files.length; i++) {
            // adiciona na lista de fotos
            photos.add(files[i].getAbsolutePath());
        }
        mainAdapter = new MainAdapter(MainActivity.this, photos);
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Util.calculateNoOfColumns(MainActivity.this, w);//  calculam quantas colunas de fotos cabem na tela do celular
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns);// configuram o RecycleView para exibir as fotos em GRID, respeitando o número máximo de colunas
        rvGallery.setLayoutManager(gridLayoutManager);
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // O método acima será chamado sempre que um item da ToolBar for selecionado. Caso o ícone de câmera tenha sido clicado, então será executado código que dispara a câmera do celular
        switch (item.getItemId()) {
            case R.id.opCamera:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startPhotoActivity(String photoPath) {
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }

    private void dispatchTakePictureIntent() {
        //método que dispara a app de câmera
        File f = null;// é criado um arquivo vazio dentro da pasta Pictures
        try {
            f = createImageFile();
        } catch (IOException e) {
            //Caso o arquivo não possa ser criado, é exibida uma mensagem para o usuário e o método retorna
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }
        currentPhotoPath = f.getAbsolutePath();// o local do mesmo é salvo no atributo de classe currentPhotoPath
        if(f != null) {
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "lorenssute.dossantos.francisco.galeria.fileprovider", f); //é gerado um endereço URI para o arquivo de foto
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// Um Intent para disparar a app de câmera é criado
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);// URI é passado para a app de câmera via Intent
            startActivityForResult(i, RESULT_TAKE_PICTURE);// app de câmera é efetivamente iniciada e a nossa app fica a espera do resultado
        }
    }
    private File createImageFile() throws IOException {
        //utiliza a data e hora para criar um nome de arquivo diferente para cada foto tirada
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_TAKE_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                //Caso a foto tenha sido tirada
                photos.add(currentPhotoPath);//o local dela é adicionado na lista de fotos
                mainAdapter.notifyItemInserted(photos.size() - 1);// MainAdapter é avisado
            }
            else {
                //Caso a foto não tenha sido tirada
                File f = new File(currentPhotoPath);
                f.delete();//o arquivo criado para conter a foto é excluído
            }
        }
    }
    private void checkForPermissions(List<String> permissions) {//exibe o dialogo ao usuário pedindo que ele confirme a permissão de uso do recurso
        List<String> permissionsNotGranted = new ArrayList<>();//aceita como entrada uma lista de permissões

        for(String permission : permissions) {
            if( !hasPermisson(permission)) {//cada permissão é verificada
                permissionsNotGranted.add(permission);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionsNotGranted.size() > 0) {
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }
    }
    private boolean hasPermisson(String permission) {//verifica se uma determinada permissão já foi concedida ou não
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(MainActivity.this,permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<String> permissionsRejected = new ArrayList<>();
        if (requestCode == RESULT_REQUEST_PERMISSION) {
            for(String permission : permissions) {
                if(!hasPermisson(permission)) {
                  permissionsRejected.add(permission);
                }
            }
        }
        if (permissionsRejected.size() > 0) {
            //Caso o usuário não tenha ainda confirmado uma permissão
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {//esta é posta em uma lista de permissões não confirmadas ainda
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }
    }
}