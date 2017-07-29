package ch.unstable.ost.api.transport;


public enum Transportation {
    ICE_TGV_RJ("ice_tgv_rj"),
    EC_IC("ec_ic"),
    INTERREGIO("ir"),
    RE_D("re_d"),
    SHIP("ship"),
    S_SN_R("s_sn_r"),
    BUS("bus"),
    CABLEWAY("cableway"),
    ARZ_EXT("arz_ext"),
    TRAMWAY_UNDERGROUND("tramway_underground");

    private final String identifier;

    Transportation(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}