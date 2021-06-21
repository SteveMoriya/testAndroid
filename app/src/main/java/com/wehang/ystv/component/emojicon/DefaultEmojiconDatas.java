package com.wehang.ystv.component.emojicon;


import com.wehang.ystv.R;

public class DefaultEmojiconDatas {

    private static int[] icons = new int[]{R.drawable.f001, R.drawable.f002, R.drawable.f003,
            R.drawable.f004, R.drawable.f005, R.drawable.f006, R.drawable.f007, R.drawable.f008, R.drawable.f009,
            R.drawable.f010, R.drawable.f011, R.drawable.f012, R.drawable.f013, R.drawable.f014, R.drawable.f015,
            R.drawable.f016, R.drawable.f017, R.drawable.f018, R.drawable.f019, R.drawable.f020, R.drawable.f021,
            R.drawable.f022, R.drawable.f023, R.drawable.f024, R.drawable.f025, R.drawable.f026, R.drawable.f027,
            R.drawable.f028, R.drawable.f029, R.drawable.f030, R.drawable.f031, R.drawable.f032, R.drawable.f033,
            R.drawable.f034, R.drawable.f035, R.drawable.f036, R.drawable.f037, R.drawable.f038, R.drawable.f039,
            R.drawable.f040, R.drawable.f041, R.drawable.f042, R.drawable.f043, R.drawable.f044, R.drawable.f045,
            R.drawable.f046, R.drawable.f047, R.drawable.f048, R.drawable.f049, R.drawable.f050, R.drawable.f051,
            R.drawable.f052, R.drawable.f053, R.drawable.f054, R.drawable.f055, R.drawable.f056, R.drawable.f057,
            R.drawable.f058, R.drawable.f059, R.drawable.f060, R.drawable.f061, R.drawable.f062, R.drawable.f063,
            R.drawable.f064, R.drawable.f065, R.drawable.f066, R.drawable.f067, R.drawable.f068, R.drawable.f069,
            R.drawable.f070, R.drawable.f071, R.drawable.f072, R.drawable.f073, R.drawable.f074, R.drawable.f075,
            R.drawable.f076, R.drawable.f077, R.drawable.f078, R.drawable.f079, R.drawable.f080, R.drawable.f081,
            R.drawable.f082, R.drawable.f083, R.drawable.f084, R.drawable.f085, R.drawable.f086, R.drawable.f087,
            R.drawable.f088, R.drawable.f089, R.drawable.f090, R.drawable.f091, R.drawable.f092, R.drawable.f093,
            R.drawable.f094, R.drawable.f095, R.drawable.f096, R.drawable.f097, R.drawable.f098, R.drawable.f099,
            R.drawable.f100, R.drawable.f101, R.drawable.f102, R.drawable.f103, R.drawable.f104, R.drawable.f105
    };

    private static final Emojicon[] DATA = createData();

    private static Emojicon[] createData() {
        Emojicon[] datas = new Emojicon[icons.length];
        for (int i = 0; i < icons.length; i++) {
            datas[i] = new Emojicon(icons[i], "[表情]", Emojicon.Type.NORMAL);
            datas[i].setIdentityCode(i);
        }
        return datas;
    }

    public static Emojicon[] getData() {
        return DATA;
    }
}
