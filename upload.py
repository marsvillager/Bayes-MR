# -*- coding : utf-8 -*-
import os
from hdfs import InsecureClient

# hdfs 目标路径
# base_path = "/TRAIN_DATA_FILE/AUSTR"
# base_path = "/TRAIN_DATA_FILE/BRAZ"
base_path = "/TEST_DATA_FILE/AUSTR"
# base_path = "/TEST_DATA_FILE/BRAZ"
# 本地需要上传的路径（相对路径）
# local_dir = "./NBCorpus/TRAIN_DATA_FILE/AUSTR"
# local_dir = "./NBCorpus/TRAIN_DATA_FILE/BRAZ"
local_dir = "./NBCorpus/TEST_DATA_FILE/AUSTR"
# local_dir = "./NBCorpus/TEST_DATA_FILE/BRAZ"

client_hdfs = InsecureClient('http://192.168.73.169:9870', user='reptile')
client_hdfs.makedirs(base_path)


def upload_train_data(path: str) -> None:
    """
    :param path: relative path of train data
    :return: none
    """
    files_list = os.listdir(path)  # all files in the current directory

    for file_name in files_list:
        local_path = os.path.join(path, file_name).replace("\\", "/")  # relative path of all files
        hdfs_path = base_path + "/" + file_name  # absolute path in hdfs

        client_hdfs.upload(hdfs_path, local_path)
        print("file:", file_name)


if __name__ == '__main__':
    upload_train_data(local_dir)
