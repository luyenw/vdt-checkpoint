import redis
import json
import os
import random

def save_to_redis(redis_client, blacklist, redis_key='blacklist'):
    # Lưu các số trong blacklist vào Redis
    for number in blacklist:
        redis_client.sadd(redis_key, number)
    print(f"Đã lưu {len(blacklist)} số vào Redis dưới key '{redis_key}'")

def select_and_save_random_numbers(input_file, output_file, redis_client, sample_ratio=0.1):
    if not os.path.exists(input_file):
        print(f"File '{input_file}' không tồn tại.")
        return

    # Đọc dữ liệu từ file đầu vào
    with open(input_file, 'r') as file:
        phone_numbers = json.load(file)

    if os.path.exists(output_file):
        # Đọc dữ liệu từ file đầu ra nếu đã tồn tại
        with open(output_file, 'r') as file:
            random_numbers = json.load(file)
        print(f"Đã đọc {len(random_numbers)} số từ file '{output_file}'")
    else:
        # Chọn ngẫu nhiên khoảng 1/10 số từ file đầu vào
        sample_size = int(len(phone_numbers) * sample_ratio)
        random_numbers = random.sample(phone_numbers, sample_size)

        # Lưu các số đã chọn vào file đầu ra
        with open(output_file, 'w') as file:
            json.dump(random_numbers, file)
        print(f"Đã lưu {sample_size} số vào file '{output_file}'")

    # Lưu các số vào Redis
    save_to_redis(redis_client, random_numbers)

if __name__ == '__main__':
    redis_client = redis.StrictRedis(host='localhost', port=6379, db=1, password='eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81')
    select_and_save_random_numbers('hashed_phone_numbers.json', 'blacklist_phone_numbers.json', redis_client)
