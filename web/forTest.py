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
    def __init__(self, username, state):
        self.username = username
        self.state = state

@app.route('/', methods=['GET', 'POST'])
def home():
    if request.method == 'GET':
        return render_template('home.html')
    else:
        if userState.query.filter_by(username=request.form['id']).first() is None:
            return "없는사람"
        else:
            tmpuser = userState.query.filter_by(username=request.form['id']).first()
            if tmpuser.userState == 1:
                tmpuser = 0
                print("불출했습니다")
            else:
                tmpuser = 1
                print("반납되었습니다")

#세션으로 찾아야?
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
        temp = userState(username=request.form['regisername'], state=0)
        db.session.add(temp)
        db.session.commit()
        return render_template('home.html')
    else:
        return render_template('register.html')

if __name__ == '__main__':
    db.create_all()
    app.run(host='0.0.0.0')


#note :
#3. db에서 삭제
#4.안드로이드 HTTP, 해싱
#4.5 어플 제작 - 가입 / 현황보기 / 불출반납 인터페이스
#5. 현황을 웹페이지에 반영해 띄워주기 + 어플에서 잘 읽을 수 있게