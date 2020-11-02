package megvii.testfacepass.pa.beans;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/9/15.
 */

public class BaoCunBean implements Parcelable {

    private Long id;
    private int port;//端口
    private String zhujiDiZhi;
    private int moban;
    private String tuisongDiZhi; //用来存信鸽token
    private String gonggao;
    private boolean isShowMoshengren;
    private boolean isShowShiPingLiu;
    private boolean isHengOrShu;
    private int yusu;
    private int yudiao;
    private int boyingren;
    private String zhanghuId;
    private String wenzi;
    private int size;
    private String touxiangzhuji;//存激活地址
    private String houtaiDiZhi;//识别回调地址
    private String xintiaoDIZhi;//心跳回调地址
    private String huiyiId;
    private String wenzi1;//存公司名称
    private int size1;
    private String guanggaojiMing;
    private String shiPingWeiZhi;
    private String zhanhuiId;
    private String zhanhuiBianMa;
    private float shibieFaZhi;
    private int shibieFaceSize;
    private float ruKuMoHuDu;
    private int ruKuFaceSize;
    private boolean isHuoTi;
    private int huoTiFZ;
    private String shualianhuidiaodizhi;
    private String dangqianChengShi2;
    private String dangqianShiJian;
    private boolean isTianQi;
    private String shangBanTime;
    private String xiaBanTime;
    private int mima;
    private int mima2;
    private String xgToken;
    private String jihuoma;
    private String appid;
    private String appkey;
    private String appurl;
    private String tishiyu;
    private String name;
    private String leixing;
    private String weizhi;
    private String ip;
    private String xuliehao;//序列号
    private String jiaoyanmima;//校验密码
    private int moshengrenPanDing;//陌生人判定次数
    private int sbsRenLian;//是不是人脸
    private boolean msrPanDing;//开启陌生人判断,提示是不是陌生人
    private int jidianqi;//继电器间隔时间关门
    private String logo;//
    private int configModel;
    private boolean isLight;

    public BaoCunBean() {
    }

    protected BaoCunBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        port = in.readInt();
        zhujiDiZhi = in.readString();
        moban = in.readInt();
        tuisongDiZhi = in.readString();
        gonggao = in.readString();
        isShowMoshengren = in.readByte() != 0;
        isShowShiPingLiu = in.readByte() != 0;
        isHengOrShu = in.readByte() != 0;
        yusu = in.readInt();
        yudiao = in.readInt();
        boyingren = in.readInt();
        zhanghuId = in.readString();
        wenzi = in.readString();
        size = in.readInt();
        touxiangzhuji = in.readString();
        houtaiDiZhi = in.readString();
        xintiaoDIZhi = in.readString();
        huiyiId = in.readString();
        wenzi1 = in.readString();
        size1 = in.readInt();
        guanggaojiMing = in.readString();
        shiPingWeiZhi = in.readString();
        zhanhuiId = in.readString();
        zhanhuiBianMa = in.readString();
        shibieFaZhi = in.readFloat();
        shibieFaceSize = in.readInt();
        ruKuMoHuDu = in.readFloat();
        ruKuFaceSize = in.readInt();
        isHuoTi = in.readByte() != 0;
        huoTiFZ = in.readInt();
        shualianhuidiaodizhi = in.readString();
        dangqianChengShi2 = in.readString();
        dangqianShiJian = in.readString();
        isTianQi = in.readByte() != 0;
        shangBanTime = in.readString();
        xiaBanTime = in.readString();
        mima = in.readInt();
        mima2 = in.readInt();
        xgToken = in.readString();
        jihuoma = in.readString();
        appid = in.readString();
        appkey = in.readString();
        appurl = in.readString();
        tishiyu = in.readString();
        name = in.readString();
        leixing = in.readString();
        weizhi = in.readString();
        ip = in.readString();
        xuliehao = in.readString();
        jiaoyanmima = in.readString();
        moshengrenPanDing = in.readInt();
        sbsRenLian = in.readInt();
        msrPanDing = in.readByte() != 0;
        jidianqi = in.readInt();
        logo = in.readString();
        configModel = in.readInt();
        isLight = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeInt(port);
        dest.writeString(zhujiDiZhi);
        dest.writeInt(moban);
        dest.writeString(tuisongDiZhi);
        dest.writeString(gonggao);
        dest.writeByte((byte) (isShowMoshengren ? 1 : 0));
        dest.writeByte((byte) (isShowShiPingLiu ? 1 : 0));
        dest.writeByte((byte) (isHengOrShu ? 1 : 0));
        dest.writeInt(yusu);
        dest.writeInt(yudiao);
        dest.writeInt(boyingren);
        dest.writeString(zhanghuId);
        dest.writeString(wenzi);
        dest.writeInt(size);
        dest.writeString(touxiangzhuji);
        dest.writeString(houtaiDiZhi);
        dest.writeString(xintiaoDIZhi);
        dest.writeString(huiyiId);
        dest.writeString(wenzi1);
        dest.writeInt(size1);
        dest.writeString(guanggaojiMing);
        dest.writeString(shiPingWeiZhi);
        dest.writeString(zhanhuiId);
        dest.writeString(zhanhuiBianMa);
        dest.writeFloat(shibieFaZhi);
        dest.writeInt(shibieFaceSize);
        dest.writeFloat(ruKuMoHuDu);
        dest.writeInt(ruKuFaceSize);
        dest.writeByte((byte) (isHuoTi ? 1 : 0));
        dest.writeInt(huoTiFZ);
        dest.writeString(shualianhuidiaodizhi);
        dest.writeString(dangqianChengShi2);
        dest.writeString(dangqianShiJian);
        dest.writeByte((byte) (isTianQi ? 1 : 0));
        dest.writeString(shangBanTime);
        dest.writeString(xiaBanTime);
        dest.writeInt(mima);
        dest.writeInt(mima2);
        dest.writeString(xgToken);
        dest.writeString(jihuoma);
        dest.writeString(appid);
        dest.writeString(appkey);
        dest.writeString(appurl);
        dest.writeString(tishiyu);
        dest.writeString(name);
        dest.writeString(leixing);
        dest.writeString(weizhi);
        dest.writeString(ip);
        dest.writeString(xuliehao);
        dest.writeString(jiaoyanmima);
        dest.writeInt(moshengrenPanDing);
        dest.writeInt(sbsRenLian);
        dest.writeByte((byte) (msrPanDing ? 1 : 0));
        dest.writeInt(jidianqi);
        dest.writeString(logo);
        dest.writeInt(configModel);
        dest.writeByte((byte) (isLight ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaoCunBean> CREATOR = new Creator<BaoCunBean>() {
        @Override
        public BaoCunBean createFromParcel(Parcel in) {
            return new BaoCunBean(in);
        }

        @Override
        public BaoCunBean[] newArray(int size) {
            return new BaoCunBean[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getZhujiDiZhi() {
        return zhujiDiZhi;
    }

    public void setZhujiDiZhi(String zhujiDiZhi) {
        this.zhujiDiZhi = zhujiDiZhi;
    }

    public int getMoban() {
        return moban;
    }

    public void setMoban(int moban) {
        this.moban = moban;
    }

    public String getTuisongDiZhi() {
        return tuisongDiZhi;
    }

    public void setTuisongDiZhi(String tuisongDiZhi) {
        this.tuisongDiZhi = tuisongDiZhi;
    }

    public String getGonggao() {
        return gonggao;
    }

    public void setGonggao(String gonggao) {
        this.gonggao = gonggao;
    }

    public boolean isShowMoshengren() {
        return isShowMoshengren;
    }

    public void setShowMoshengren(boolean showMoshengren) {
        isShowMoshengren = showMoshengren;
    }

    public boolean isShowShiPingLiu() {
        return isShowShiPingLiu;
    }

    public void setShowShiPingLiu(boolean showShiPingLiu) {
        isShowShiPingLiu = showShiPingLiu;
    }

    public boolean isHengOrShu() {
        return isHengOrShu;
    }

    public void setHengOrShu(boolean hengOrShu) {
        isHengOrShu = hengOrShu;
    }

    public int getYusu() {
        return yusu;
    }

    public void setYusu(int yusu) {
        this.yusu = yusu;
    }

    public int getYudiao() {
        return yudiao;
    }

    public void setYudiao(int yudiao) {
        this.yudiao = yudiao;
    }

    public int getBoyingren() {
        return boyingren;
    }

    public void setBoyingren(int boyingren) {
        this.boyingren = boyingren;
    }

    public String getZhanghuId() {
        return zhanghuId;
    }

    public void setZhanghuId(String zhanghuId) {
        this.zhanghuId = zhanghuId;
    }

    public String getWenzi() {
        return wenzi;
    }

    public void setWenzi(String wenzi) {
        this.wenzi = wenzi;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTouxiangzhuji() {
        return touxiangzhuji;
    }

    public void setTouxiangzhuji(String touxiangzhuji) {
        this.touxiangzhuji = touxiangzhuji;
    }

    public String getHoutaiDiZhi() {
        return houtaiDiZhi;
    }

    public void setHoutaiDiZhi(String houtaiDiZhi) {
        this.houtaiDiZhi = houtaiDiZhi;
    }

    public String getXintiaoDIZhi() {
        return xintiaoDIZhi;
    }

    public void setXintiaoDIZhi(String xintiaoDIZhi) {
        this.xintiaoDIZhi = xintiaoDIZhi;
    }

    public String getHuiyiId() {
        return huiyiId;
    }

    public void setHuiyiId(String huiyiId) {
        this.huiyiId = huiyiId;
    }

    public String getWenzi1() {
        return wenzi1;
    }

    public void setWenzi1(String wenzi1) {
        this.wenzi1 = wenzi1;
    }

    public int getSize1() {
        return size1;
    }

    public void setSize1(int size1) {
        this.size1 = size1;
    }

    public String getGuanggaojiMing() {
        return guanggaojiMing;
    }

    public void setGuanggaojiMing(String guanggaojiMing) {
        this.guanggaojiMing = guanggaojiMing;
    }

    public String getShiPingWeiZhi() {
        return shiPingWeiZhi;
    }

    public void setShiPingWeiZhi(String shiPingWeiZhi) {
        this.shiPingWeiZhi = shiPingWeiZhi;
    }

    public String getZhanhuiId() {
        return zhanhuiId;
    }

    public void setZhanhuiId(String zhanhuiId) {
        this.zhanhuiId = zhanhuiId;
    }

    public String getZhanhuiBianMa() {
        return zhanhuiBianMa;
    }

    public void setZhanhuiBianMa(String zhanhuiBianMa) {
        this.zhanhuiBianMa = zhanhuiBianMa;
    }

    public float getShibieFaZhi() {
        return shibieFaZhi;
    }

    public void setShibieFaZhi(float shibieFaZhi) {
        this.shibieFaZhi = shibieFaZhi;
    }

    public int getShibieFaceSize() {
        return shibieFaceSize;
    }

    public void setShibieFaceSize(int shibieFaceSize) {
        this.shibieFaceSize = shibieFaceSize;
    }

    public float getRuKuMoHuDu() {
        return ruKuMoHuDu;
    }

    public void setRuKuMoHuDu(float ruKuMoHuDu) {
        this.ruKuMoHuDu = ruKuMoHuDu;
    }

    public int getRuKuFaceSize() {
        return ruKuFaceSize;
    }

    public void setRuKuFaceSize(int ruKuFaceSize) {
        this.ruKuFaceSize = ruKuFaceSize;
    }

    public boolean isHuoTi() {
        return isHuoTi;
    }

    public void setHuoTi(boolean huoTi) {
        isHuoTi = huoTi;
    }

    public int getHuoTiFZ() {
        return huoTiFZ;
    }

    public void setHuoTiFZ(int huoTiFZ) {
        this.huoTiFZ = huoTiFZ;
    }

    public String getShualianhuidiaodizhi() {
        return shualianhuidiaodizhi;
    }

    public void setShualianhuidiaodizhi(String shualianhuidiaodizhi) {
        this.shualianhuidiaodizhi = shualianhuidiaodizhi;
    }

    public String getDangqianChengShi2() {
        return dangqianChengShi2;
    }

    public void setDangqianChengShi2(String dangqianChengShi2) {
        this.dangqianChengShi2 = dangqianChengShi2;
    }

    public String getDangqianShiJian() {
        return dangqianShiJian;
    }

    public void setDangqianShiJian(String dangqianShiJian) {
        this.dangqianShiJian = dangqianShiJian;
    }

    public boolean isTianQi() {
        return isTianQi;
    }

    public void setTianQi(boolean tianQi) {
        isTianQi = tianQi;
    }

    public String getShangBanTime() {
        return shangBanTime;
    }

    public void setShangBanTime(String shangBanTime) {
        this.shangBanTime = shangBanTime;
    }

    public String getXiaBanTime() {
        return xiaBanTime;
    }

    public void setXiaBanTime(String xiaBanTime) {
        this.xiaBanTime = xiaBanTime;
    }

    public int getMima() {
        return mima;
    }

    public void setMima(int mima) {
        this.mima = mima;
    }

    public int getMima2() {
        return mima2;
    }

    public void setMima2(int mima2) {
        this.mima2 = mima2;
    }

    public String getXgToken() {
        return xgToken;
    }

    public void setXgToken(String xgToken) {
        this.xgToken = xgToken;
    }

    public String getJihuoma() {
        return jihuoma;
    }

    public void setJihuoma(String jihuoma) {
        this.jihuoma = jihuoma;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppurl() {
        return appurl;
    }

    public void setAppurl(String appurl) {
        this.appurl = appurl;
    }

    public String getTishiyu() {
        return tishiyu;
    }

    public void setTishiyu(String tishiyu) {
        this.tishiyu = tishiyu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeixing() {
        return leixing;
    }

    public void setLeixing(String leixing) {
        this.leixing = leixing;
    }

    public String getWeizhi() {
        return weizhi;
    }

    public void setWeizhi(String weizhi) {
        this.weizhi = weizhi;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getXuliehao() {
        return xuliehao;
    }

    public void setXuliehao(String xuliehao) {
        this.xuliehao = xuliehao;
    }

    public String getJiaoyanmima() {
        return jiaoyanmima;
    }

    public void setJiaoyanmima(String jiaoyanmima) {
        this.jiaoyanmima = jiaoyanmima;
    }

    public int getMoshengrenPanDing() {
        return moshengrenPanDing;
    }

    public void setMoshengrenPanDing(int moshengrenPanDing) {
        this.moshengrenPanDing = moshengrenPanDing;
    }

    public int getSbsRenLian() {
        return sbsRenLian;
    }

    public void setSbsRenLian(int sbsRenLian) {
        this.sbsRenLian = sbsRenLian;
    }

    public boolean isMsrPanDing() {
        return msrPanDing;
    }

    public void setMsrPanDing(boolean msrPanDing) {
        this.msrPanDing = msrPanDing;
    }

    public int getJidianqi() {
        return jidianqi;
    }

    public void setJidianqi(int jidianqi) {
        this.jidianqi = jidianqi;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getConfigModel() {
        return configModel;
    }

    public void setConfigModel(int configModel) {
        this.configModel = configModel;
    }

    public boolean isLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }
}
