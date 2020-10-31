from flask import Flask, request, render_template
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

#설정
app.config['SECRET_KEY'] = 'this is secret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///user.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

#모델 : id, username(폰 고유값), state(불출/반납 상태), password(군번)
class userState(db.Model):
    __table_name__ = 'user_state'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    state = db.Column(db.Integer)
    password = db.Column(db.String(80))
    def __repr__(self):
        return '<User %r>' % self.username
    def __init__(self, username, state, password):
        self.username = username
        self.state = state
        self.password = password

@app.route('/', methods=['GET', 'POST'])
def home():
    if request.method == 'GET':
        return render_template('home.html')
    else:
        if userState.query.filter_by(password=request.form['pw']).first().state == 1:
            userState.query.filter_by(password=request.form['pw']).first().state = 0
            return "불출"
        tmpuser = userState.query.filter_by(username=request.form['name']).first()
        if tmpuser is None:
            return "없는사람"
        else:
            if tmpuser.state == 1:
                tmpuser.state = 0
                db.session.commit()
                return "불출"
            else:
                tmpuser.state = 1
                db.session.commit()
                return "반납"

#세션으로 찾아야?
@app.route('/viewState')
def viewState():
    data = userState.query.all()
    return render_template('viewState.html', data_list=data)

@app.route('/register', methods=['GET', 'POST'])
def register():
    #db에 데이터 추가
    if request.method == 'POST':
        temp = userState(username=request.form['name'], state=0, password=request.form['pw'])
        db.session.add(temp)
        db.session.commit()
        return render_template('home.html')
    else:
        return render_template('register.html')

if __name__ == '__main__':
    db.create_all()
    app.run(host='0.0.0.0', port=5000)
