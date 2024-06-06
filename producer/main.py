import hashlib
import json
import os
import random
import string
import time
import numpy as np
from datetime import datetime
from scipy.optimize import fsolve
from kafka import KafkaProducer
import threading
import uuid 
import datetime

def generate_data(number_of_phone_numbers=1000000, file_path='hashed_phone_numbers.json'):
    # Tạo mã thuê bao ngẫu nhiên
    def generate_phone_number():
        return ''.join(random.choices(string.digits, k=10))
    # Mã hóa số thuê bao
    def hash_phone_number(phone_number):
        return hashlib.sha256(phone_number.encode()).hexdigest()
    # Tạo kho số thuê bao và mã hóa chúng
    def generate_hashed_phone_numbers(quantity):
        phone_numbers = [generate_phone_number() for _ in range(quantity)]
        hashed_phone_numbers = [hash_phone_number(number) for number in phone_numbers]
        return hashed_phone_numbers
    # Lưu kho số thuê bao vào file
    def save_phone_numbers_to_file(phone_numbers, file_path):
        with open(file_path, 'w') as file:
            json.dump(phone_numbers, file)
    # Kiểm tra và tạo kho số thuê bao nếu file chưa tồn tại
    if not os.path.exists(file_path):
        phone_numbers = generate_hashed_phone_numbers(number_of_phone_numbers)
        save_phone_numbers_to_file(phone_numbers, file_path)
        print(f"Đã lưu {number_of_phone_numbers} số thuê bao mã hóa vào file '{file_path}'")
    else:
        print(f"File '{file_path}' đã tồn tại, bỏ qua bước tạo mới.")

def produce_message():
    # Đọc kho số thuê bao từ file
    def load_phone_numbers_from_file(file_path='hashed_phone_numbers.json'):
        with open(file_path, 'r') as file:
            return json.load(file)

    def estimate_gamma_params(value_range):
        lower_bound, upper_bound = value_range
        mean = (upper_bound + lower_bound) / 2
        std = (upper_bound - lower_bound) / 2

        def equations(params):
            shape, scale = params
            return [shape * scale - mean, np.sqrt(shape) * scale - std]

        shape, scale = fsolve(equations, (1.0, 1.0))
        return shape, scale
    # Hàm để sinh thời lượng cuộc gọi sử dụng phân phối gamma
    def generate_call_duration():
        shape, scale = estimate_gamma_params((30, 300))
        return int(np.random.gamma(shape, scale))

    # Hàm để sinh dữ liệu cuộc gọi từ kho số thuê bao
    def generate_call_data(phone_numbers, busy_numbers):
        # available_numbers = list(set(phone_numbers) - busy_numbers)
        # if len(available_numbers) < 2:
            # return None  # Không đủ số thuê bao để tạo cuộc gọi

        ma_gui = random.choice(phone_numbers)
        # available_numbers.remove(ma_gui)
        ma_nhan = random.choice(phone_numbers)
        
        # busy_numbers.add(ma_gui)
        # busy_numbers.add(ma_nhan)
        
        return {
            'id': str(uuid.uuid4()),
            'ma_nhan': ma_nhan,
            'ma_gui': ma_gui,
            'thoi_luong_cuoc_goi': generate_call_duration(),
            'thoi_gian_goi': datetime.datetime.now(tz=datetime.timezone.utc).isoformat()
        }

    # Hàm để gửi dữ liệu vào Kafka topic từ một luồng riêng biệt
    def send_data_to_kafka(producer, phone_numbers, busy_numbers, topic):
        try:
            while True:
                call_data = generate_call_data(phone_numbers, busy_numbers)
                if call_data:
                    producer.send(topic, value=call_data)
                    # busy_numbers.remove(call_data['ma_gui'])
                    # busy_numbers.remove(call_data['ma_nhan'])
                    # producer.flush()
                else:
                    print("Không đủ số thuê bao khả dụng để tạo cuộc gọi.")
        except KeyboardInterrupt:
            print("Stopping producer")
        finally:
            producer.close()

    # Số luồng bạn muốn tạo
    num_threads = 10

    # Đọc kho số thuê bao từ file
    file_path = 'hashed_phone_numbers.json'
    phone_numbers = load_phone_numbers_from_file(file_path)
    busy_numbers = set()

    # Tạo Kafka producer
    producer = KafkaProducer(
        bootstrap_servers='localhost:9092', 
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    topic = 'in'

    # Tạo và bắt đầu các luồng
    threads = []
    for _ in range(num_threads):
        thread = threading.Thread(target=send_data_to_kafka, args=(producer, phone_numbers, busy_numbers, topic))
        thread.daemon = True  # Đánh dấu luồng là luồng daemon để nó sẽ kết thúc khi chương trình chính kết thúc
        thread.start()
        threads.append(thread)
    try:
        for thread in threads:
            thread.join()
    except KeyboardInterrupt:
        print("Stopping producer")
    finally:
        producer.close()
if __name__ == '__main__':
    generate_data()
    produce_message()
    