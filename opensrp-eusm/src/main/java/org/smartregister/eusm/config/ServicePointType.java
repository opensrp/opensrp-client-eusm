package org.smartregister.eusm.config;

import androidx.annotation.DrawableRes;

import org.smartregister.eusm.R;

public enum ServicePointType {
    CSB1("cbs1", R.drawable.ic_health_sp, "cbs1"),
    CSB2("cbs2", R.drawable.ic_health_sp, "cbs2"),
    CHRD1("chrd1", R.drawable.ic_hospital_sp, "chrd1"),
    CHRD2("chrd2", R.drawable.ic_hospital_sp, "chrd2"),
    CHRR("chrr", R.drawable.ic_hospital_sp, "chrr"),
    SDSP("sdsp", R.drawable.ic_gov_sp, "sdsp"),
    DRSP("drsp", R.drawable.ic_gov_sp, "drsp"),
    MSP("msp", R.drawable.ic_gov_sp, "msp"),
    EPP("epp", R.drawable.ic_school_sp, "epp"),
    CEG("ceg", R.drawable.ic_school_sp, "ceg"),
    WAREHOUSE("warehouse", R.drawable.ic_warehouse_sp, "Warehouse"),
    WATERPOINT("waterpoint", R.drawable.ic_wash_sp, "Water Point"),
    PRESCO("presco", R.drawable.ic_school_sp, "presco"),
    MEAH("meah", R.drawable.ic_wash_sp, "meah"),
    DREAH("dreah", R.drawable.ic_wash_sp, "dreah"),
    MPPSPF("mppspf", R.drawable.ic_hq_sp, "mppspf"),
    DRPPSPF("drppspf", R.drawable.ic_gov_sp, "drppspf"),
    NGOPARTNER("ngopartner", R.drawable.ic_map_sp, "NGO Partner"),
    SITECOMMUNAUTAIRE("sitecommunautaire", R.drawable.ic_distribution_site_sp, "Site Communautaire"),
    DRJS("drjs", R.drawable.ic_gov_sp, "drjs"),
    INSTAT("instat", R.drawable.ic_gov_sp, "instat"),
    BSD("bsd", R.drawable.ic_gov_sp, "bsd");

    public String name;

    public String text;

    public int drawableId;

    ServicePointType(String name, @DrawableRes int drawableId, String text) {
        this.name = name;
        this.drawableId = drawableId;
        this.text = text;
    }
}
