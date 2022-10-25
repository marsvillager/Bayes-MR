# -*- coding : utf-8 -*-
import os
from hdfs import InsecureClient

# hdfs 目标路径
base_path = "/NBCorpus/Country/CANA"
# 本地需要上传的路径（相对路径）
dir = "./NBCorpus/Country/CANA"

client_hdfs = InsecureClient('http://192.168.73.169:9870', user='reptile')
client_hdfs.makedirs(base_path)


def get_all_dir(path: str):
    fills_list = os.listdir(path)

    for file_name in fills_list:
        file_abs_path = os.path.join(path, file_name).replace("\\", "/")

        # dir
        if os.path.isdir(file_abs_path):
            global dir_count
            client_hdfs.makedirs(base_path + "/" + file_name)

            print("dir:", file_name)
            get_all_dir(file_abs_path)
        # file
        else:
            mid_path = file_abs_path.replace(dir, "").replace(file_name, "")
            hdfs_path = base_path + mid_path + "" + file_name
            local_path = file_abs_path
            print("hdfs_path: " + hdfs_path + "\n" + "local_path: " + local_path)

            client_hdfs.upload(hdfs_path, file_abs_path)
            print("file:", file_name)


if __name__ == '__main__':
    get_all_dir(dir)

