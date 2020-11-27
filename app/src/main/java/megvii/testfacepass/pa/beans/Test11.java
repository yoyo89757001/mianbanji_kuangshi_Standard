package megvii.testfacepass.pa.beans;

import java.util.List;

public class Test11 {


    /**
     * result : [{"departmentName":"开发部","image":"http://hy.inteyeligence.com/userfiles/fileupload/202010/1319919543435210753.jpg","shortId":"438274333","name":"玉建华","pepopleType":"0","id":"1319917307254616064","command":"1001"},{"departmentName":"开发部","image":"http://hy.inteyeligence.com/userfiles/fileupload/202010/1319919543435210753.jpg","shortId":"438274333","name":"玉建华","pepopleType":"0","id":"1319917307254616064","command":"1004"},{"departmentName":"开发部","image":"http://hy.inteyeligence.com/userfiles/fileupload/202010/1319562257116192770.jpg","shortId":"435592076","name":"陈军","pepopleType":"0","id":"1319562290507046912","command":"1001"},{"departmentName":"开发部","image":"http://hy.inteyeligence.com/userfiles/fileupload/202010/1319562257116192770.jpg","shortId":"435592076","name":"陈军","pepopleType":"0","id":"1319562290507046912","command":"1004"},{"departmentName":"开发部","image":"http://hy.inteyeligence.com/userfiles/fileupload/202011/1327524294142861314.jpg","shortId":"431013993","name":"噢噢","pepopleType":"0","id":"1268030781836111872","command":"1001"},{"departmentName":"开发部","image":"http://hy.inteyeligence.com/userfiles/fileupload/202011/1327524294142861314.jpg","shortId":"431013993","name":"噢噢","pepopleType":"0","id":"1268030781836111872","command":"1004"}]
     * code : 0
     * desc : 有新的指令！
     */

    private int code;
    private String desc;
    private List<ResultDTO> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ResultDTO> getResult() {
        return result;
    }

    public void setResult(List<ResultDTO> result) {
        this.result = result;
    }

    public static class ResultDTO {
        /**
         * departmentName : 开发部
         * image : http://hy.inteyeligence.com/userfiles/fileupload/202010/1319919543435210753.jpg
         * shortId : 438274333
         * name : 玉建华
         * pepopleType : 0
         * id : 1319917307254616064
         * command : 1001
         */

        private String departmentName;
        private String image;
        private String shortId;
        private String name;
        private String pepopleType;
        private String id;
        private String command;

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPepopleType() {
            return pepopleType;
        }

        public void setPepopleType(String pepopleType) {
            this.pepopleType = pepopleType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }
}
