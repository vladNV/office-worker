const vm = {
    uploading: document.getElementById("uploading"),
    converting: document.getElementById("converting"),
    downloading: document.getElementById("downloading"),
    excelFile: document.getElementById("excel-file"),
    formExcelUploading: document.getElementById("form-excel-uploading"),
    formTemplate: document.getElementById("form-template"),
    pdfTemplate: document.getElementById("pdf-template"),
    excelFileStatus: document.getElementById("excel-file-status"),
    mask: document.getElementById("mask"),
    downloadLinks: document.getElementById("download-links"),
    path: '',
}

const FILENAME_REGEX = /[A-ZА-яa-zа-я_\s]+.(xls|xlsx)/i;

function uploadFile() {
   let fileName = vm.excelFile.value;
//    if (!FILENAME_REGEX.test(fileName)) {
//        vm.excelFileStatus.textContent = "Неправильное расширение и имя файла!"
//        return;
//    }
   loadExcelFile().then(path => vm.path = path).then(path => alert('Файл загружен успешно!')).catch(err => alert(err));
}

function convert() {
    if (vm.path) {
        let convertingRequest = {
            path: vm.path,
            pdfTemplate: vm.pdfTemplate.options[vm.pdfTemplate.selectedIndex].textContent
        };
        convertPdfFile(convertingRequest).then(link => {
            vm.downloadLinks.innerHTML = 
            '<a href="' 
            + window.location.origin 
            + '/document/download?filename='
            + link 
            + '">Нажмите для скачивания<a/>'
        }).then(link => alert("Файл успешнено преобразован")).catch(err => alert(err));
    }
}

function loadExcelFile() {
    return new Promise(function(resolve, reject) {
        let file = vm.excelFile.files[0];
        let formData = new FormData();
        formData.append("excel", file);
    
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/document/upload', true)
        xhr.upload.onprogress = function(){
            vm.mask.style.display = 'block';
        };
        xhr.onload = function() {
            if (this.status != 200) {
                vm.mask.style.display = 'none';
                reject(error);
            } else {
                vm.mask.style.display = 'none';
                return resolve(this.response);
            }
        };
        xhr.send(formData);
    });
}


function convertPdfFile(data) {
    return new Promise(function(resolve, reject) {
        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/document/convert', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.upload.onprogress = function(){
            vm.mask.style.display = 'block';
        };
        xhr.onload = function() {
            if (this.status != 200) {
                vm.mask.style.display = 'none';
                reject(error);
            } else {
                vm.mask.style.display = 'none';
                return resolve(this.response);
            }
        };
        xhr.send(JSON.stringify(data));
    });
}

function getAllPdfTemplates() {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/document/templates', false);
    xhr.send();

    if (xhr.status == 200) {
        let options = JSON.parse(xhr.response);
        for(index in options) {
            vm.pdfTemplate.options[vm.pdfTemplate.options.length] = new Option(options[index], index);
        }
    }
}