package lorenssute.dossantos.francisco.galeria;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter {
    MainActivity mainActivity;
    List<String> photos;

    public MainAdapter(MainActivity mainActivity, List<String> photos) {
        this.mainActivity = mainActivity;
        this.photos = photos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ImageView imPhoto = holder.itemView.findViewById(R.id.imItem);
        int w = (int)   // obtidos as dimensões que a imagem vai ter na lista
        mainActivity.getResources().getDimension(R.dimen.itemWidth);
        int h = (int) // obtidos as dimensões que a imagem vai ter na lista
        mainActivity.getResources().getDimension(R.dimen.itemHeight);
        Bitmap bitmap = Util.getBitmap(photos.get(position), w, h); //carrega a imagem em um Bitmap ao mesmo tempo em que a foto é escalada para casar com os tamanhos definidos para o ImageView
        imPhoto.setImageBitmap(bitmap); //  Bitmap é setado no ImageView
        imPhoto.setOnClickListener(new View.OnClickListener() {
            // é definido o que acontece quando o usuário clica em cima de uma imagem: a app navega para PhotoActivity, cuja função é exibir a foto e tamanho ampliado
            @Override
            public void onClick(View v) {
                mainActivity.startPhotoActivity(photos.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
