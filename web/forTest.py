from flask import Flask, request, render_template
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

#설정
app.config['SECRET_KEY'] = 'this is secret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///user.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

#모델 : id, username(폰 고유값), state(불출/반납 상태)
class userState(db.Model):
    __table_name__ = 'user_state'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    state = db.Column(db.Integer)
    def __repr__(self):
        return '<User %r>' % self.username
    def __init__(self, _username, _state):
        self.username = _username
        self.state = _state

#불러올 때
#userState.query.all()
#Model 클래스 참고

@app.route('/', methods=['GET', 'POST'])
def home():
    if request.method == 'GET':
        return render_template('home.html')
    else:
        mid = request.form['id']
        mpw = request.form['pw']
        return mid+mpw
        #여기다 state 갈 수 있게
        # return render_template('viewState.html')

#세션으로 찾아야
@app.route('/viewState')
def viewState():
    data = userState.query.all()
    for tempdata in data:
        print(tempdata.username + "  " + str(tempdata.state))
    return 'callstate'

@app.route('/register', methods=['GET', 'POST'])
def register():
    #db에 데이터 추가
    if request.method == 'POST':
        temp = userState(_username=request.form['regisername'], _state=0)
        db.session.add(temp)
        db.session.commit()
        return render_template('home.html')
    else:
        return render_template('register.html')

if __name__ == '__main__':
    app.run()


#note :
#######깃허브 업로드하기
#3. db에서 삭제
#4.안드로이드 HTTP, 
#4.5 어플 제작 - 가입 / 현황보기 / 불출반납 인터페이스
#5. 현황을 웹페이지에 반영해 띄워주기 + 어플에서 잘 읽을 수 있게
#A 유저네임을 고유한 값 해싱해서 데이터베이스에 저장
#?? NFC 어케활용할까? 반납함의 태그를 찍는 것으로 특정? 태그를 실시간 변화하게?? 자동생성된 QR코드??