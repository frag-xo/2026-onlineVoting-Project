let UPLOAD = (function () {
  function UPLOAD () {}
  // From表单验证规则  可用于公用的校验部分
  UPLOAD.Upload = function () {
    return {


      handleSuccess(response, file, fileList) {
        this.$success("上传成功");
      },
      handleError() {
        this.$error("上传失败,请重新上传图片!");
      },
      handleBeforeUpload(file,type,vm) {
        const isImage = file.type.includes("image");
        if (!isImage) {
          vm.$message.error("上传文件类型必须是图片!");
        }
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
          vm.$message.error("上传图片大小不能超过 2MB!");
        }
        if(isImage && isLt2M){//如果同时满足大小和类型则把上次传的图片清理调用
          this.removeUploadedFile(type,vm);
        }
        return isImage && isLt2M;
      },
      handleProgress(event, file, fileList) {
        this.loading = true;  //  上传时执行loading事件
      },
      handleChange(file, fileList, type,vm,name,modelname) {
        //  限制单张上传，超过限制即覆盖
        console.log("----------so----");
        console.log(fileList);
        if (fileList.length > 1) {
          fileList.splice(0, 1);
        }

        //  校验
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
          vm.$message.error("上传图片大小不能超过 2MB!");
          this.removeUploadedFile(type,vm);  //  不符合要求删除文件
          return false;
        }

        const isImage = file.raw.type.includes("image");
        if (!isImage) {
          vm.$message.error("上传的格式必须是图片!");
          this.removeUploadedFile(type,vm);
          return false;
        }
        //console.log(vm.$refs[name]);
        //  验证通过之后，将缓存区文件存入上传区文件中
        vm.$data.formData.files[name] = file.raw;
        console.log(vm.$data.formData.files[name])
        vm.uploadImg(modelname,name,file);


      },

      handleChanges(file, fileList, type,vm) {
        //  限制单张上传，超过限制即覆盖

        if (fileList.length > 1) {
          fileList.splice(0, 1);
        }

        //  校验
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
          vm.$message.error("上传图片大小不能超过 2MB!");
          this.removeUploadedFile(type,vm);  //  不符合要求删除文件
          return false;
        }

        const isImage = file.raw.type.includes("image");
        if (!isImage) {
          vm.$message.error("上传的格式必须是图片!");
          this.removeUploadedFile(type,vm);
          return false;
        }
        //console.log(vm.$refs[name]);
        //  验证通过之后，将缓存区文件存入上传区文件中
        vm.$data.formData.files[type] = file.raw;
        console.log(vm.$data.formData.files[type])
      },
//  从缓存区移除文件
      removeUploadedFile(type,vm) {
        if (type === 0) {
          vm.$refs.file1.clearFiles();
        }
        if (type === 1) {
          vm.$refs.file2.clearFiles();
        }
        if (type === 2) {
          vm.$refs.file3.clearFiles();
        }
      },
//  删除文件
      handleRemove(type,vm,file, fileList) {

        //  删除文件时要移除缓存区文件和上传区文件
        Array.prototype.remove = function(v) {
          console.log("------------------000000000000--");
          console.log(this.length);
          if (isNaN(v) || v > this.length) {
            return -1
          }
          for (let i = 0, j = 0; i < this.length; i++) {
            if (this[i] != this[v]) {
              this[j++] = this[i]
            }
          }
          this.length -= 1
        }
        var index = vm.fileList.indexOf(file);
        vm.fileList.remove(index);
        //vm.uploadFile.remove(index);
        return index;
      },
      uploadMutiImg(vm) {
        //  校验是否选择文件
        let fileNum = this.flatten(vm.uploadFile).length;
        if (fileNum === 0) {
          console.log("未选择任何文件!");
          return false;
        }

        //  使用formdata格式
        let formData = new FormData();
        if (vm.formData.files[0]) {
          formData.append("file1", vm.formData.files[0]);
        }
        if (vm.formData.files[1]) {
          formData.append("file2", vm.formData.files[1]);
        }
        if (vm.formData.files[2]) {
          formData.append("file2", vm.formData.files[2]);
        }

        //  请求:在headers上务必加上content-Type,指定表单形式发送
        api
          .post("uploadUrl", formData, {headers: { "Content-Type": "multipart/form-data" }})
          .then(res => {
            vm.oldImg = true;
            vm.$success("上传图片成功!");
            vm.fileList = [[],[],[]];
            vm.uploadFile =[[],[],[]];
          })
          .catch(e => {
            console.log(e);
          });

      },
      //  扁平化数组
      flatten(arr) {
        let res = [];
        for (let i = 0; i < arr.length; i++) {
          if (Array.isArray(arr[i])) {
            res = res.concat(this.flatten(arr[i]));
          } else {
            res.push(arr[i]);
          }
        }
        return res;
      },
      //删除指定索引的字符串
      delStringByIndex(oldStr,index){
        let oldStrArray = oldStr.split(",");
        let newStrArray = new Array();
        for (i=0;i<oldStrArray.length;i++){
          if (i != index ){//如果本次循环的下标不等于要剔除的下标将，对应元素放入新数组
            newStrArray.push(oldStrArray[i]);
          }
        }
        return newStrArray.join(",");
      }
    }
  }

  return UPLOAD
}())

exports.UPLOAD = UPLOAD

