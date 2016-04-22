package phamngoctuan.mymusicplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by phamn_000 on 4/5/2016.
 */

public class FragmentListSong extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);

        ListView list = (ListView)rootView.findViewById(R.id.playlist);
        MainActivity.list = list;

        CustomArrayAdapter adapter = new CustomArrayAdapter(getContext(), R.layout.list_item, MainActivity.listSongs);
        if (list != null)
            list.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
