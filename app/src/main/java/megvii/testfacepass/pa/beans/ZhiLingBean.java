package megvii.testfacepass.pa.beans;

import java.util.List;

public class ZhiLingBean {


    /**
     * code : 0
     * desc : 成功
     * result : [{"command":1001,"id":"12345","name":"张三","departmentName":"技术部","pepopleType":"0","image":"http://www.123.com","cardID":"12345","shortId":"12345678"},{"command":1001,"id":"123456","name":"李四","departmentName":"销售部","pepopleType":"0","image":"http://www.123.com","cardID":"12345","shortId":"123456789"}]
     */

    private int code;
    private String message;
    private List<ResultBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getData() {
        return data;
    }

    public void setData(List<ResultBean> data) {
        this.data = data;
    }

    public static class ResultBean {
        /**
         * command : 1001
         * id : 12345
         * name : 张三
         * departmentName : 技术部
         * pepopleType : 0
         * image : http://www.123.com
         * cardID : 12345
         * shortId : 12345678
         */

        private int command;
        private String id;
        private String name;
        private String departmentName;
        private String pepopleType;
        private String image;
        private String cardID;
        private String shortId;
        private int isOpen;//0关 1 开
        private String startPeriods;
        private String endPeriods;

        public String getStartPeriods() {
            return startPeriods;
        }

        public void setStartPeriods(String startPeriods) {
            this.startPeriods = startPeriods;
        }

        public String getEndPeriods() {
            return endPeriods;
        }

        public void setEndPeriods(String endPeriods) {
            this.endPeriods = endPeriods;
        }

        public int getIsOpen() {
            return isOpen;
        }

        public void setIsOpen(int isOpen) {
            this.isOpen = isOpen;
        }

        public int getCommand() {
            return command;
        }

        public void setCommand(int command) {
            this.command = command;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public String getPepopleType() {
            return pepopleType;
        }

        public void setPepopleType(String pepopleType) {
            this.pepopleType = pepopleType;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCardID() {
            return cardID;
        }

        public void setCardID(String cardID) {
            this.cardID = cardID;
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }
    }
}
